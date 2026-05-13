package com.proyecto.keeply.controllers;

import com.proyecto.keeply.entities.ContenidoUsuario;
import com.proyecto.keeply.services.ContenidoUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de exponer la API para la gestión de "Contenidos de Usuario".
 * Maneja las peticiones HTTP que crean, leen, actualizan o borran datos extra
 * que un usuario añade a un ítem de su biblioteca (notas adicionales, reseñas, etc).
 */
@RestController
@RequestMapping("/api/contenidos")
@RequiredArgsConstructor
public class ContenidoUsuarioController {

    private final ContenidoUsuarioService contenidoUsuarioService;

    /**
     * Obtiene todos los contenidos de la base de datos global.
     * @return Lista general de ContenidoUsuario y estado 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<ContenidoUsuario>> getAllContenidos() {
        return ResponseEntity.ok(contenidoUsuarioService.findAll());
    }

    /**
     * Obtiene un contenido específico mediante su ID numérico.
     * @param id ID único del contenido.
     * @return 200 OK si lo encuentra, o 404 Not Found si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContenidoUsuario> getContenidoById(@PathVariable Integer id) {
        return contenidoUsuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene todos los contenidos secundarios que estén enlazados a un ítem concreto de una biblioteca.
     * @param itemUsuarioId ID del registro principal en la biblioteca.
     * @return 200 OK con la lista de contenidos vinculados.
     */
    @GetMapping("/item/{itemUsuarioId}")
    public ResponseEntity<List<ContenidoUsuario>> getContenidosByItem(@PathVariable Integer itemUsuarioId) {
        return ResponseEntity.ok(contenidoUsuarioService.findByItemUsuarioId(itemUsuarioId));
    }

    /**
     * Crea un nuevo registro de contenido para el usuario.
     * @param contenidoUsuario Objeto JSON con los detalles (texto, referencias...).
     * @return 201 Created con el objeto ya persistido y su ID autogenerado.
     */
    @PostMapping
    public ResponseEntity<ContenidoUsuario> createContenido(@RequestBody ContenidoUsuario contenidoUsuario) {
        ContenidoUsuario saved = contenidoUsuarioService.save(contenidoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Modifica de manera completa (PUT) un contenido existente.
     * @param id El ID del contenido a modificar.
     * @param contenidoUsuario El objeto con la información actualizada.
     * @return 200 OK con el contenido modificado, o 404 Not Found si el ID no existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContenidoUsuario> updateContenido(@PathVariable Integer id, 
                                                             @RequestBody ContenidoUsuario contenidoUsuario) {
        // Valida que el registro exista antes de intentar actualizar
        if (contenidoUsuarioService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Asegura que el ID de la ruta predomine sobre el cuerpo (seguridad básica)
        contenidoUsuario.setIdContenido(id);
        return ResponseEntity.ok(contenidoUsuarioService.save(contenidoUsuario));
    }

    /**
     * Elimina permanentemente un contenido de usuario.
     * @param id El ID del contenido a eliminar.
     * @return 204 No Content confirmando el borrado, o 404 si no existía.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContenido(@PathVariable Integer id) {
        if (contenidoUsuarioService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        contenidoUsuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
