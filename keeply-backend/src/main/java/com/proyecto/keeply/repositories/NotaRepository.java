package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.ItemUsuario;
import com.proyecto.keeply.entities.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio de Spring Data JPA encargado de gestionar los comentarios/notas
 * que los usuarios dejan en sus elementos de la biblioteca.
 */
public interface NotaRepository extends JpaRepository<Nota, Integer> {
    
    /**
     * Recupera todas las notas asociadas a una entrada de la biblioteca en particular,
     * y las ordena automáticamente desde la más reciente a la más antigua.
     */
    List<Nota> findByItemUsuarioOrderByFechaCreacionDesc(ItemUsuario itemUsuario);
}
