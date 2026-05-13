package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.Seguimiento;
import com.proyecto.keeply.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio de Spring Data JPA para manejar el sistema social de Follows.
 */
public interface SeguimientoRepository extends JpaRepository<Seguimiento, Integer> {
    
    /** Busca todas las relaciones donde el usuario especificado es el que sigue a otros (A quién sigo). */
    List<Seguimiento> findBySeguidor(Usuario seguidor);
    
    /** Busca todas las relaciones donde el usuario especificado es el seguido (Mis fans). */
    List<Seguimiento> findBySeguido(Usuario seguido);
}
