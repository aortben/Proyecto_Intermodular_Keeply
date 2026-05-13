package com.proyecto.keeply.controllers;

import com.proyecto.keeply.dto.AuthResponseDTO;
import com.proyecto.keeply.dto.GoogleAuthRequestDTO;
import com.proyecto.keeply.dto.LoginRequestDTO;
import com.proyecto.keeply.dto.RegisterRequestDTO;
import com.proyecto.keeply.entities.Usuario;
import com.proyecto.keeply.repositories.UsuarioRepository;
import com.proyecto.keeply.security.JwtService;
import com.proyecto.keeply.services.GoogleAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST que gestiona la autenticación de usuarios.
 * Proporciona endpoints para inicio de sesión, registro y autenticación mediante Google.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthService googleAuthService;

    /**
     * Autentica a un usuario utilizando credenciales tradicionales (usuario y contraseña).
     * @param request DTO con las credenciales de inicio de sesión.
     * @return ResponseEntity con el token JWT y los datos del usuario si es exitoso, o un error 401.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            // Verifica las credenciales contra la base de datos a través de Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getNombreUsuario(), request.getContrasena()));

            // Recupera la entidad del usuario una vez autenticado correctamente
            Usuario usuario = usuarioRepository.findByNombreUsuario(request.getNombreUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Genera el token JWT para mantener la sesión del usuario
            String token = jwtService.generateToken(usuario.getNombreUsuario());

            // Construye la respuesta con el token y los datos básicos del perfil (incluyendo avatar)
            AuthResponseDTO response = AuthResponseDTO.builder()
                    .token(token)
                    .idUsuario(usuario.getIdUsuario())
                    .nombreUsuario(usuario.getNombreUsuario())
                    .email(usuario.getEmail())
                    .avatarUrl(usuario.getAvatarUrl())
                    .customBanners(usuario.getCustomBanners())
                    .build();

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            // Devuelve error 401 si el usuario o contraseña son incorrectos
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param request DTO con los datos de registro (usuario, email, contraseña, avatar).
     * @return ResponseEntity con los datos del usuario creado y su token JWT, o error si hay duplicados.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        // Valida que el nombre de usuario no exista previamente en la base de datos
        if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nombre de usuario ya está en uso"));
        }

        // Valida que el correo electrónico no esté registrado por otro usuario
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El email ya está registrado"));
        }

        // Crea la entidad de usuario encriptando la contraseña antes de guardarla
        Usuario usuario = Usuario.builder()
                .nombreUsuario(request.getNombreUsuario())
                .email(request.getEmail())
                .contrasenaHash(passwordEncoder.encode(request.getContrasena()))
                .avatarUrl(request.getAvatarUrl())
                .build();

        Usuario saved = usuarioRepository.save(usuario);

        // Genera un token JWT automáticamente para iniciar sesión tras el registro exitoso
        String token = jwtService.generateToken(saved.getNombreUsuario());

        AuthResponseDTO response = AuthResponseDTO.builder()
                .token(token)
                .idUsuario(saved.getIdUsuario())
                .nombreUsuario(saved.getNombreUsuario())
                .email(saved.getEmail())
                .avatarUrl(saved.getAvatarUrl())
                .customBanners(saved.getCustomBanners())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Autentica a un usuario utilizando un token de Google OAuth2.
     * Si es la primera vez, crea una cuenta automáticamente.
     * @param request DTO que contiene el token de credencial proporcionado por Google.
     * @return ResponseEntity con el token JWT propio del sistema y los datos del usuario.
     */
    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@RequestBody GoogleAuthRequestDTO request) {
        try {
            // Delega la validación del token de Google y la gestión del usuario al servicio correspondiente
            AuthResponseDTO response = googleAuthService.authenticateWithGoogle(request.getCredential());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Devuelve error si el token de Google es inválido o expiró
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
