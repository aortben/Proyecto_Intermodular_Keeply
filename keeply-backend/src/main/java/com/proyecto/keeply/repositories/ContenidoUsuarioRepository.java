package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.ContenidoUsuario;
import com.proyecto.keeply.entities.ItemUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio de Spring Data JPA para gestionar los Contenidos Extra (ContenidoUsuario)
 * vinculados a la biblioteca de un usuario.
 */
public interface ContenidoUsuarioRepository extends JpaRepository<ContenidoUsuario, Integer> {
    
    /**
     * Devuelve todos los contenidos asociados a un ítem de la biblioteca.
     */
    List<ContenidoUsuario> findByItemUsuario(ItemUsuario itemUsuario);
}
