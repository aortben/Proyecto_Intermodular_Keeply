package com.proyecto.keeply.services;

import com.proyecto.keeply.entities.Seguimiento;
import com.proyecto.keeply.entities.Usuario;
import com.proyecto.keeply.repositories.SeguimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar las relaciones sociales entre usuarios (sistema de seguidores).
 * Maneja la lógica de quién sigue a quién dentro de la plataforma.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SeguimientoService {

    private final SeguimientoRepository seguimientoRepository;
    private final UsuarioService usuarioService;

    /**
     * Devuelve todas las relaciones de seguimiento globales (normalmente uso administrativo).
     */
    public List<Seguimiento> findAll() {
        return seguimientoRepository.findAll();
    }

    /**
     * Busca una relación de seguimiento concreta por su ID.
     * @param id ID del seguimiento.
     */
    public Optional<Seguimiento> findById(Integer id) {
        return seguimientoRepository.findById(id);
    }

    /**
     * Obtiene la lista de usuarios a los que una persona está siguiendo (sus ídolos/amigos).
     * @param seguidorId El ID del usuario que sigue a otros.
     * @return Lista de relaciones de seguimiento iniciadas por este usuario.
     */
    public List<Seguimiento> findBySeguidorId(Integer seguidorId) {
        return usuarioService.findById(seguidorId)
                .map(seguimientoRepository::findBySeguidor)
                .orElse(List.of());
    }

    /**
     * Obtiene la lista de seguidores que tiene una persona (sus fans).
     * @param seguidoId El ID del usuario que es seguido.
     * @return Lista de relaciones donde este usuario es el objetivo.
     */
    public List<Seguimiento> findBySeguidoId(Integer seguidoId) {
        return usuarioService.findById(seguidoId)
                .map(seguimientoRepository::findBySeguido)
                .orElse(List.of());
    }

    /**
     * Crea una nueva relación de seguimiento (Usuario A sigue al Usuario B).
     * Incorpora validación de negocio para evitar auto-seguimiento.
     * @param seguimiento Objeto de la relación a guardar.
     * @return La relación de seguimiento confirmada.
     * @throws IllegalArgumentException si un usuario intenta seguirse a sí mismo.
     */
    public Seguimiento save(Seguimiento seguimiento) {
        // Validar que no se siga a sí mismo (Regla de negocio crítica)
        if (seguimiento.getSeguidor().getIdUsuario().equals(seguimiento.getSeguido().getIdUsuario())) {
            throw new IllegalArgumentException("Un usuario no puede seguirse a sí mismo");
        }
        return seguimientoRepository.save(seguimiento);
    }

    /**
     * Elimina un seguimiento por su ID (Usuario A deja de seguir al Usuario B).
     * @param id ID de la relación a destruir.
     */
    public void deleteById(Integer id) {
        seguimientoRepository.deleteById(id);
    }

    /**
     * Comprueba de manera rápida si existe una relación de seguimiento dada.
     */
    public boolean existsById(Integer id) {
        return seguimientoRepository.existsById(id);
    }
}
