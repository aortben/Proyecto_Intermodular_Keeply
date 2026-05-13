package com.proyecto.keeply.services;

import com.proyecto.keeply.entities.ContenidoUsuario;
import com.proyecto.keeply.entities.ItemUsuario;
import com.proyecto.keeply.repositories.ContenidoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con el "Contenido de Usuario"
 * (como notas, valoraciones adicionales o detalles específicos que un usuario asocia a un ítem de su biblioteca).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ContenidoUsuarioService {

    private final ContenidoUsuarioRepository contenidoUsuarioRepository;
    private final ItemUsuarioService itemUsuarioService;

    /**
     * Recupera todos los contenidos de usuario de la base de datos.
     */
    public List<ContenidoUsuario> findAll() {
        return contenidoUsuarioRepository.findAll();
    }

    /**
     * Busca un contenido de usuario por su ID único.
     * @param id Identificador del contenido.
     * @return Un Optional que puede contener el contenido si se encuentra.
     */
    public Optional<ContenidoUsuario> findById(Integer id) {
        return contenidoUsuarioRepository.findById(id);
    }

    /**
     * Recupera todos los contenidos asociados a un ítem específico de un usuario.
     * @param itemUsuarioId Identificador del ItemUsuario en la biblioteca.
     * @return Lista de contenidos relacionados.
     */
    public List<ContenidoUsuario> findByItemUsuarioId(Integer itemUsuarioId) {
        return itemUsuarioService.findById(itemUsuarioId)
                .map(contenidoUsuarioRepository::findByItemUsuario)
                .orElse(List.of());
    }

    /**
     * Guarda o actualiza un contenido de usuario en la base de datos.
     * @param contenidoUsuario Entidad a guardar.
     * @return La entidad guardada.
     */
    public ContenidoUsuario save(ContenidoUsuario contenidoUsuario) {
        return contenidoUsuarioRepository.save(contenidoUsuario);
    }

    /**
     * Elimina un contenido de usuario por su ID.
     * @param id Identificador del contenido a borrar.
     */
    public void deleteById(Integer id) {
        contenidoUsuarioRepository.deleteById(id);
    }
}
