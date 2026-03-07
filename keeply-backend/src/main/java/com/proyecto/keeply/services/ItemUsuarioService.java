package com.proyecto.keeply.services;

import com.proyecto.keeply.entities.ItemUsuario;
import com.proyecto.keeply.entities.Usuario;
import com.proyecto.keeply.repositories.ItemUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemUsuarioService {

    private final ItemUsuarioRepository itemUsuarioRepository;
    private final UsuarioService usuarioService;

    public List<ItemUsuario> findAll() {
        return itemUsuarioRepository.findAll();
    }

    public Optional<ItemUsuario> findById(Integer id) {
        return itemUsuarioRepository.findById(id);
    }

    public List<ItemUsuario> findByUsuarioId(Integer usuarioId) {
        return usuarioService.findById(usuarioId)
                .map(itemUsuarioRepository::findByUsuario)
                .orElse(List.of());
    }

    public ItemUsuario save(ItemUsuario itemUsuario) {
        return itemUsuarioRepository.save(itemUsuario);
    }

    public void deleteById(Integer id) {
        itemUsuarioRepository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return itemUsuarioRepository.existsById(id);
    }
}

