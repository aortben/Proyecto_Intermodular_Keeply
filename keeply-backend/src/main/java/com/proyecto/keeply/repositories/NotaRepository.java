package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.ItemUsuario;
import com.proyecto.keeply.entities.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Integer> {
    List<Nota> findByItemUsuarioOrderByFechaCreacionDesc(ItemUsuario itemUsuario);
}
