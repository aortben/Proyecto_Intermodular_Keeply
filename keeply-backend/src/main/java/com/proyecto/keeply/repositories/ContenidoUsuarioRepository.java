package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.ContenidoUsuario;
import com.proyecto.keeply.entities.ItemUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContenidoUsuarioRepository extends JpaRepository<ContenidoUsuario, Integer> {
    List<ContenidoUsuario> findByItemUsuario(ItemUsuario itemUsuario);
}
