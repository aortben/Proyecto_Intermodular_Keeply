package com.proyecto.keeply.controllers;

import com.proyecto.keeply.dto.AuthResponseDTO;
import com.proyecto.keeply.dto.LoginRequestDTO;
import com.proyecto.keeply.dto.RegisterRequestDTO;
import com.proyecto.keeply.entities.Usuario;
import com.proyecto.keeply.repositories.UsuarioRepository;
import com.proyecto.keeply.security.JwtService;
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
                    .build();

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        // Verificar que nombre de usuario no exista
        if (usuarioRepository.findByNombreUsuario(request.getNombreUsuario()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nombre de usuario ya está en uso"));
        }

        // Crear usuario con contraseña encriptada
        Usuario usuario = Usuario.builder()
                .nombreUsuario(request.getNombreUsuario())
                .contrasenaHash(passwordEncoder.encode(request.getContrasena()))
                .build();

        Usuario saved = usuarioRepository.save(usuario);

        // Generar token automáticamente tras el registro
        String token = jwtService.generateToken(saved.getNombreUsuario());

        AuthResponseDTO response = AuthResponseDTO.builder()
                .token(token)
                .idUsuario(saved.getIdUsuario())
                .nombreUsuario(saved.getNombreUsuario())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
