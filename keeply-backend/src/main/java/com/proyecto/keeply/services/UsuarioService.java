package com.proyecto.keeply.services;

import com.proyecto.keeply.entities.Usuario;
import com.proyecto.keeply.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar las operaciones CRUD y lógicas relacionadas con la entidad Usuario.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Recupera todos los usuarios registrados en el sistema.
     * @return Lista de usuarios.
     */
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su identificador único.
     * @param id ID numérico del usuario.
     * @return Un Optional que contiene el usuario si se encuentra.
     */
    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Busca un usuario mediante su nombre de usuario (username).
     * @param nombreUsuario Nombre único de cuenta.
     * @return Un Optional con el usuario si coincide.
     */
    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    /**
     * Busca un usuario mediante su dirección de correo electrónico.
     * Útil para inicios de sesión o comprobaciones de duplicidad en el registro.
     * @param email Correo electrónico a buscar.
     * @return Un Optional con el usuario si existe.
     */
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Persiste un usuario nuevo o actualiza uno existente en la base de datos.
     * @param usuario Entidad con los datos del usuario.
     * @return El usuario ya guardado y con su ID generado (si era nuevo).
     */
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario del sistema basándose en su ID.
     * @param id ID del usuario a eliminar.
     */
    public void deleteById(Integer id) {
        usuarioRepository.deleteById(id);
    }

    /**
     * Verifica de forma rápida si un usuario existe sin tener que cargar toda su entidad.
     * @param id ID a comprobar.
     * @return true si existe, false si no.
     */
    public boolean existsById(Integer id) {
        return usuarioRepository.existsById(id);
    }
}
