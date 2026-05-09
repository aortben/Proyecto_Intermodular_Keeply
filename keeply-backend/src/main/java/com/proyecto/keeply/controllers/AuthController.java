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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthService googleAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getNombreUsuario(), request.getContrasena()));

            Usuario usuario = usuarioRepository.findByNombreUsuario(request.getNombreUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String token = jwtService.generateToken(usuario.getNombreUsuario());

            AuthResponseDTO response = AuthResponseDTO.builder()
                    .token(token)
                    .idUsuario(usuario.getIdUsuario())
                    .nombreUsuario(usuario.getNombreUsuario())
                    .email(usuario.getEmail())
                    .build();

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        // Verificar que nombre de usuario no exista
        if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nombre de usuario ya está en uso"));
        }

        // Verificar que email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El email ya está registrado"));
        }

        // Crear usuario con contraseña encriptada
        Usuario usuario = Usuario.builder()
                .nombreUsuario(request.getNombreUsuario())
                .email(request.getEmail())
                .contrasenaHash(passwordEncoder.encode(request.getContrasena()))
                .build();

        Usuario saved = usuarioRepository.save(usuario);

        // Generar token automáticamente tras el registro
        String token = jwtService.generateToken(saved.getNombreUsuario());

        AuthResponseDTO response = AuthResponseDTO.builder()
                .token(token)
                .idUsuario(saved.getIdUsuario())
                .nombreUsuario(saved.getNombreUsuario())
                .email(saved.getEmail())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@RequestBody GoogleAuthRequestDTO request) {
        try {
            AuthResponseDTO response = googleAuthService.authenticateWithGoogle(request.getCredential());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
