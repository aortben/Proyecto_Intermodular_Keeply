package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.Obra;
import com.proyecto.keeply.entities.TipoObra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ObraRepository extends JpaRepository<Obra, Integer> {
    List<Obra> findByTipoObra(TipoObra tipoObra);
    List<Obra> findByTituloContainingIgnoreCase(String titulo);
}
