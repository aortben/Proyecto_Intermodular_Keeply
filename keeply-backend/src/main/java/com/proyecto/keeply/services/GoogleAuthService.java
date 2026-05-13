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

/**
 * Servicio encargado de gestionar el flujo de autenticación mediante Google OAuth2.
 * Válida el token proporcionado por el frontend contra los servidores de Google y
 * enlaza la cuenta de Google con el sistema de usuarios de Keeply.
 */
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    // ID del cliente de Google configurado en el panel de Google Cloud Console
    @Value("${google.client.id}")
    private String googleClientId;

    /**
     * Flujo principal de inicio de sesión o registro automático con Google.
     * @param credential El token JWT (Credential) devuelto por Google Identity Services en el frontend.
     * @return AuthResponseDTO con el token JWT de nuestra app y los datos del usuario.
     */
    public AuthResponseDTO authenticateWithGoogle(String credential) {
        // 1. Verificamos la autenticidad del token comunicándonos con Google
        GoogleIdToken.Payload payload = verifyGoogleToken(credential);

        // Extraemos la información básica pública del perfil de Google
        String email = payload.getEmail();
        String nombre = (String) payload.get("name");
        String avatarUrl = (String) payload.get("picture");

        // Si el usuario no tiene nombre en Google, generamos uno a partir de su correo (lo que va antes del @)
        String nombreUsuario = nombre != null ? nombre : email.split("@")[0];

        // 2. Buscamos si este correo ya existe en nuestra base de datos
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(email);

        Usuario usuario;
        if (existingUser.isPresent()) {
            usuario = existingUser.get();
            
            // Si el correo existe pero se registró de forma tradicional (con contraseña), bloqueamos para evitar secuestro de cuenta
            if (usuario.getAuthProvider() != AuthProvider.GOOGLE) {
                throw new RuntimeException("Este email ya está registrado con usuario y contraseña. Usa el login normal.");
            }
            
            // Si el avatar en Google ha cambiado, lo actualizamos en nuestra base de datos
            if (avatarUrl != null && !avatarUrl.equals(usuario.getAvatarUrl())) {
                usuario.setAvatarUrl(avatarUrl);
                usuario = usuarioRepository.save(usuario);
            }
        } else {
            // 3. Si el usuario no existe, lo creamos automáticamente (Registro automático vía Google)
            
            // Validamos que el nombre de usuario generado no choque con uno existente
            String finalNombreUsuario = nombreUsuario;
            int counter = 1;
            while (usuarioRepository.existsByNombreUsuario(finalNombreUsuario)) {
                // Si existe "juan", probamos con "juan1", "juan2", etc.
                finalNombreUsuario = nombreUsuario + counter;
                counter++;
            }

            // Construimos el nuevo usuario indicando que proviene de GOOGLE
            usuario = Usuario.builder()
                    .nombreUsuario(finalNombreUsuario)
                    .email(email)
                    .authProvider(AuthProvider.GOOGLE)
                    .contrasenaHash("GOOGLE_AUTH_NO_PASSWORD") // Se asigna una clave ficticia, no será usada
                    .avatarUrl(avatarUrl)
                    .build();
            usuario = usuarioRepository.save(usuario);
        }

        // 4. Generamos nuestro propio token JWT para mantener la sesión abierta en el frontend
        String token = jwtService.generateToken(usuario.getNombreUsuario());

        // Devolvemos el DTO con toda la información necesaria para la interfaz
        return AuthResponseDTO.builder()
                .token(token)
                .idUsuario(usuario.getIdUsuario())
                .nombreUsuario(usuario.getNombreUsuario())
                .email(usuario.getEmail())
                .avatarUrl(usuario.getAvatarUrl())
                .customBanners(usuario.getCustomBanners())
                .build();
    }

    /**
     * Verifica criptográficamente el token de Google contra su clave pública.
     * @param idTokenString El token en formato String.
     * @return El Payload verificado con los datos del usuario.
     * @throws RuntimeException Si el token ha expirado, fue manipulado, o el Audience no coincide.
     */
    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            // Instancia el verificador configurado con nuestro googleClientId (Audience)
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            // Valida la firma del token
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
