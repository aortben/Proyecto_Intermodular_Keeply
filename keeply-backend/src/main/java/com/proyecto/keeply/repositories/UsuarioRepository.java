package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad Usuario.
 * Gestiona automáticamente el acceso a la base de datos (CRUD) sin necesidad de escribir SQL.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    /** Busca un usuario por su nombre exacto (alias). */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    
    /** Busca un usuario por su correo electrónico (útil para login/recuperación). */
    Optional<Usuario> findByEmail(String email);
    
    /** Comprueba rápidamente si un correo ya está registrado en la BD. */
    boolean existsByEmail(String email);
    
    /** Comprueba rápidamente si un nombre de usuario ya está ocupado. */
    boolean existsByNombreUsuario(String nombreUsuario);
}
