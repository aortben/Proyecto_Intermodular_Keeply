package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.Adjunto;
import com.proyecto.keeply.entities.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio de Spring Data JPA para la gestión de Archivos Adjuntos (fotos, videos, etc.)
 * subidos a las notas.
 */
public interface AdjuntoRepository extends JpaRepository<Adjunto, Integer> {
    
    /**
     * Devuelve todos los adjuntos que pertenecen a una nota específica.
     */
    List<Adjunto> findByNota(Nota nota);
}
