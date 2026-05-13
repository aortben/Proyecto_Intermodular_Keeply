package com.proyecto.keeply.services;

import com.proyecto.keeply.entities.ItemUsuario;
import com.proyecto.keeply.entities.Usuario;
import com.proyecto.keeply.repositories.ItemUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar los "Ítems de Usuario", es decir, 
 * las entradas específicas (libros, juegos, animes, etc.) que un usuario tiene guardadas en su biblioteca personal.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ItemUsuarioService {

    private final ItemUsuarioRepository itemUsuarioRepository;
    private final UsuarioService usuarioService;

    /**
     * Recupera todos los ítems guardados por todos los usuarios.
     * (Método generalmente usado para administración).
     */
    public List<ItemUsuario> findAll() {
        return itemUsuarioRepository.findAll();
    }

    /**
     * Busca un ítem concreto de una biblioteca mediante su ID.
     * @param id Identificador del ítem.
     * @return Un Optional con el ítem si existe.
     */
    public Optional<ItemUsuario> findById(Integer id) {
        return itemUsuarioRepository.findById(id);
    }

    /**
     * Devuelve toda la biblioteca (lista de ítems) perteneciente a un usuario específico.
     * @param usuarioId ID del usuario propietario.
     * @return Lista de ítems en la biblioteca del usuario.
     */
    public List<ItemUsuario> findByUsuarioId(Integer usuarioId) {
        return usuarioService.findById(usuarioId)
                .map(itemUsuarioRepository::findByUsuario)
                .orElse(List.of());
    }

    /**
     * Añade o actualiza un ítem en la biblioteca de un usuario.
     * @param itemUsuario La entidad a persistir.
     * @return El ítem guardado.
     */
    public ItemUsuario save(ItemUsuario itemUsuario) {
        return itemUsuarioRepository.save(itemUsuario);
    }

    /**
     * Elimina un ítem de la biblioteca.
     * @param id ID del ítem a borrar.
     */
    public void deleteById(Integer id) {
        itemUsuarioRepository.deleteById(id);
    }

    /**
     * Comprueba si un ítem de usuario existe en la base de datos.
     * @param id ID del ítem.
     * @return true si existe, false en caso contrario.
     */
    public boolean existsById(Integer id) {
        return itemUsuarioRepository.existsById(id);
    }
}
