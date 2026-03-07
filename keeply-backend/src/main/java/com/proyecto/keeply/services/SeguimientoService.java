package com.proyecto.keeply.services;

import com.proyecto.keeply.entities.Seguimiento;
import com.proyecto.keeply.entities.Usuario;
import com.proyecto.keeply.repositories.SeguimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SeguimientoService {

    private final SeguimientoRepository seguimientoRepository;
    private final UsuarioService usuarioService;

    public List<Seguimiento> findAll() {
        return seguimientoRepository.findAll();
    }

    public Optional<Seguimiento> findById(Integer id) {
        return seguimientoRepository.findById(id);
    }

    public List<Seguimiento> findBySeguidorId(Integer seguidorId) {
        return usuarioService.findById(seguidorId)
                .map(seguimientoRepository::findBySeguidor)
                .orElse(List.of());
    }

    public List<Seguimiento> findBySeguidoId(Integer seguidoId) {
        return usuarioService.findById(seguidoId)
                .map(seguimientoRepository::findBySeguido)
                .orElse(List.of());
    }

    public Seguimiento save(Seguimiento seguimiento) {
        // Validar que no se siga a sí mismo
        if (seguimiento.getSeguidor().getIdUsuario().equals(seguimiento.getSeguido().getIdUsuario())) {
            throw new IllegalArgumentException("Un usuario no puede seguirse a sí mismo");
        }
        return seguimientoRepository.save(seguimiento);
    }

    public void deleteById(Integer id) {
        seguimientoRepository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return seguimientoRepository.existsById(id);
    }
}

