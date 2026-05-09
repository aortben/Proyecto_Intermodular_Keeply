package com.proyecto.keeply.security;

import com.proyecto.keeply.entities.AuthProvider;
import com.proyecto.keeply.entities.Usuario;
import com.proyecto.keeply.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + nombreUsuario));

        // Los usuarios de Google tienen un placeholder como contraseña
        String password = usuario.getContrasenaHash();
        if (usuario.getAuthProvider() == AuthProvider.GOOGLE) {
            password = "GOOGLE_AUTH_NO_PASSWORD";
        }

        return new User(
                usuario.getNombreUsuario(),
                password,
                Collections.emptyList());
    }
}
