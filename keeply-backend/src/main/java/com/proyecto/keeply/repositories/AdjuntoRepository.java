package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.Adjunto;
import com.proyecto.keeply.entities.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdjuntoRepository extends JpaRepository<Adjunto, Integer> {
    List<Adjunto> findByNota(Nota nota);
}
