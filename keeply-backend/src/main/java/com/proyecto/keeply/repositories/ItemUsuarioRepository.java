package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.ItemUsuario;
import com.proyecto.keeply.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio de Spring Data JPA para la entidad ItemUsuario (La Biblioteca Personal).
 * Proporciona métodos para consultar qué obras tiene guardadas un usuario.
 */
public interface ItemUsuarioRepository extends JpaRepository<ItemUsuario, Integer> {
    
    /**
     * Recupera todos los registros de biblioteca asociados a un usuario específico.
     * Es equivalente a hacer un "SELECT * FROM Item_Usuario WHERE id_usuario = X".
     */
    List<ItemUsuario> findByUsuario(Usuario usuario);
}
