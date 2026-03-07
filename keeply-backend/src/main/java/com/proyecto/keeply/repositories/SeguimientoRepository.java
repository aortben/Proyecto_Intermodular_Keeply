package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.Seguimiento;
import com.proyecto.keeply.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeguimientoRepository extends JpaRepository<Seguimiento, Integer> {
    List<Seguimiento> findBySeguidor(Usuario seguidor);
    List<Seguimiento> findBySeguido(Usuario seguido);
}
