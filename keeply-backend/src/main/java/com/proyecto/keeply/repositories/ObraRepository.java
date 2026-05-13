package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.Obra;
import com.proyecto.keeply.entities.TipoObra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio de Spring Data JPA para el catálogo de Obras.
 * Centraliza las consultas sobre películas, libros, juegos, etc. que ya han sido
 * registrados localmente en la base de datos.
 */
public interface ObraRepository extends JpaRepository<Obra, Integer> {
    
    /**
     * Filtra las obras de la base de datos por su categoría (Ej. Solo 'PELICULA').
     */
    List<Obra> findByTipoObra(TipoObra tipoObra);
    
    /**
     * Realiza una búsqueda mediante LIKE (%titulo%) ignorando mayúsculas.
     * Útil para buscadores internos.
     */
    List<Obra> findByTituloContainingIgnoreCase(String titulo);
}
