package com.proyecto.keeply.services;

import com.proyecto.keeply.entities.ContenidoUsuario;
import com.proyecto.keeply.entities.ItemUsuario;
import com.proyecto.keeply.repositories.ContenidoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContenidoUsuarioService {

    private final ContenidoUsuarioRepository contenidoUsuarioRepository;
    private final ItemUsuarioService itemUsuarioService;

    public List<ContenidoUsuario> findAll() {
        return contenidoUsuarioRepository.findAll();
    }

    public Optional<ContenidoUsuario> findById(Integer id) {
        return contenidoUsuarioRepository.findById(id);
    }

    public List<ContenidoUsuario> findByItemUsuarioId(Integer itemUsuarioId) {
        return itemUsuarioService.findById(itemUsuarioId)
                .map(contenidoUsuarioRepository::findByItemUsuario)
                .orElse(List.of());
    }

    public ContenidoUsuario save(ContenidoUsuario contenidoUsuario) {
        return contenidoUsuarioRepository.save(contenidoUsuario);
    }

    public void deleteById(Integer id) {
        contenidoUsuarioRepository.deleteById(id);
    }
}

