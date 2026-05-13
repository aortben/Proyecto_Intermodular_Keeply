package com.proyecto.keeply.controllers;

import com.proyecto.keeply.entities.Usuario;
import com.proyecto.keeply.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST encargado de la gestión de las cuentas de Usuario.
 * Permite realizar operaciones CRUD completas y actualizaciones parciales 
 * como cambios de avatar o personalización de banners.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Lista a todos los usuarios del sistema.
     * @return 200 OK con la lista completa.
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    /**
     * Obtiene el perfil de un usuario específico mediante su ID numérico.
     * @param id ID del usuario.
     * @return 200 OK con el perfil, o 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Integer id) {
        return usuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene el perfil de un usuario mediante su nombre de usuario (alias/username).
     * Es ideal para generar perfiles públicos (ej. /usuario/juan).
     * @param nombreUsuario String exacto del nombre.
     */
    @GetMapping("/nombre/{nombreUsuario}")
    public ResponseEntity<Usuario> getUsuarioByNombre(@PathVariable String nombreUsuario) {
        return usuarioService.findByNombreUsuario(nombreUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea un usuario manualmente desde el panel de administración o durante un registro.
     * (NOTA: El flujo estándar de registro público usa AuthController).
     * @param usuario Datos del usuario a insertar.
     */
    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
        Usuario saved = usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Actualiza la entidad completa de un usuario.
     * @param id El ID del usuario.
     * @param usuario Los nuevos datos.
     * @return 200 OK con el usuario modificado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Integer id, @RequestBody Usuario usuario) {
        if (!usuarioService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuario.setIdUsuario(id);
        return ResponseEntity.ok(usuarioService.save(usuario));
    }

    /**
     * Actualiza exclusivamente la URL del avatar del usuario.
     * Usado por el componente Navbar en frontend para cambiar la foto rápidamente.
     * 
     * @param id ID del usuario.
     * @param body JSON que contiene el campo "avatarUrl".
     * @return 200 OK con el nuevo avatar en formato JSON.
     */
    @PutMapping("/{id}/avatar")
    public ResponseEntity<?> updateAvatar(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        return usuarioService.findById(id)
                .map(usuario -> {
                    // Sustituye el avatar antiguo por el nuevo que envía el cliente
                    usuario.setAvatarUrl(body.get("avatarUrl"));
                    usuarioService.save(usuario);
                    return ResponseEntity.ok(Map.of("avatarUrl", usuario.getAvatarUrl() != null ? usuario.getAvatarUrl() : ""));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualiza los banners personalizados de las bibliotecas del usuario.
     * Los banners se almacenan como un String serializado en JSON dentro de la base de datos.
     * 
     * @param id ID del usuario.
     * @param body JSON que contiene el campo "customBanners".
     * @return 200 OK con el JSON actualizado.
     */
    @PutMapping("/{id}/banners")
    public ResponseEntity<?> updateBanners(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        return usuarioService.findById(id)
                .map(usuario -> {
                    // Guarda la preferencia estética del usuario
                    usuario.setCustomBanners(body.get("customBanners"));
                    usuarioService.save(usuario);
                    return ResponseEntity.ok(Map.of("customBanners", usuario.getCustomBanners() != null ? usuario.getCustomBanners() : "{}"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Borra la cuenta completa de un usuario del sistema.
     * @param id ID a dar de baja.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id) {
        if (!usuarioService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
