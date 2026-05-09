package com.proyecto.keeply.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.proyecto.keeply.dto.AuthResponseDTO;
import com.proyecto.keeply.entities.AuthProvider;
import com.proyecto.keeply.entities.Usuario;
import com.proyecto.keeply.repositories.UsuarioRepository;
import com.proyecto.keeply.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Value("${google.client.id}")
    private String googleClientId;

    /**
     * Verifica el ID token de Google, busca o crea al usuario en la BD,
     * y genera un JWT propio de Keeply.
     */
    public AuthResponseDTO authenticateWithGoogle(String credential) {
        GoogleIdToken.Payload payload = verifyGoogleToken(credential);

        String email = payload.getEmail();
        String nombre = (String) payload.get("name");
        String avatarUrl = (String) payload.get("picture");

        // Generar un nombre de usuario a partir del email si no hay nombre
        String nombreUsuario = nombre != null ? nombre : email.split("@")[0];

        // Buscar usuario existente por email
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(email);

        Usuario usuario;
        if (existingUser.isPresent()) {
            usuario = existingUser.get();
            // Verificar que el usuario sea de tipo Google
            if (usuario.getAuthProvider() != AuthProvider.GOOGLE) {
                throw new RuntimeException("Este email ya está registrado con usuario y contraseña. Usa el login normal.");
            }
            // Actualizar avatar si cambió
            if (avatarUrl != null && !avatarUrl.equals(usuario.getAvatarUrl())) {
                usuario.setAvatarUrl(avatarUrl);
                usuario = usuarioRepository.save(usuario);
            }
        } else {
            // Verificar que el nombre de usuario no esté en uso
            String finalNombreUsuario = nombreUsuario;
            int counter = 1;
            while (usuarioRepository.existsByNombreUsuario(finalNombreUsuario)) {
                finalNombreUsuario = nombreUsuario + counter;
                counter++;
            }

            // Crear nuevo usuario Google
            usuario = Usuario.builder()
                    .nombreUsuario(finalNombreUsuario)
                    .email(email)
                    .authProvider(AuthProvider.GOOGLE)
                    .contrasenaHash("GOOGLE_AUTH_NO_PASSWORD")
                    .avatarUrl(avatarUrl)
                    .build();
            usuario = usuarioRepository.save(usuario);
        }

        // Generar JWT propio de Keeply
        String token = jwtService.generateToken(usuario.getNombreUsuario());

        return AuthResponseDTO.builder()
                .token(token)
                .idUsuario(usuario.getIdUsuario())
                .nombreUsuario(usuario.getNombreUsuario())
                .email(usuario.getEmail())
                .build();
    }

    /**
     * Verifica el ID token de Google usando la librería oficial.
     */
    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new RuntimeException("Token de Google inválido");
            }
            return idToken.getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar el token de Google: " + e.getMessage(), e);
        }
    }
}
