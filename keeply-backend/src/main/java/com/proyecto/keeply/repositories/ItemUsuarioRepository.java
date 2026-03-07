package com.proyecto.keeply.repositories;

import com.proyecto.keeply.entities.ItemUsuario;
import com.proyecto.keeply.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemUsuarioRepository extends JpaRepository<ItemUsuario, Integer> {
    List<ItemUsuario> findByUsuario(Usuario usuario);
}
