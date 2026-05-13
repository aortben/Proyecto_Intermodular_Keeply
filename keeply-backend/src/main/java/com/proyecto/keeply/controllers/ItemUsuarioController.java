package com.proyecto.keeply.controllers;

import com.proyecto.keeply.entities.ItemUsuario;
import com.proyecto.keeply.services.ItemUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que expone las operaciones de la Biblioteca Personal ("ItemUsuario").
 * Permite listar, agregar, actualizar y borrar elementos (películas, libros, etc.) 
 * que un usuario en particular ha decidido guardar o consumir.
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemUsuarioController {

    private final ItemUsuarioService itemUsuarioService;

    /**
     * Obtiene absolutamente todos los ítems de todos los usuarios en la plataforma.
     * @return 200 OK con la lista completa.
     */
    @GetMapping
    public ResponseEntity<List<ItemUsuario>> getAllItems() {
        return ResponseEntity.ok(itemUsuarioService.findAll());
    }

    /**
     * Obtiene un registro individual de biblioteca basado en su ID único.
     * @param id ID interno de la entrada (ItemUsuario).
     * @return 200 OK con el objeto si se encuentra, 404 si no.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemUsuario> getItemById(@PathVariable Integer id) {
        return itemUsuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retorna la "Biblioteca de un Usuario", es decir, la lista completa de ítems 
     * asociados a un usuario en concreto.
     * @param usuarioId ID del dueño de la biblioteca.
     * @return 200 OK con la colección de ítems.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ItemUsuario>> getItemsByUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(itemUsuarioService.findByUsuarioId(usuarioId));
    }

    /**
     * Registra un nuevo elemento en la biblioteca de alguien.
     * @param itemUsuario Objeto JSON representando el ítem (incluye relación a la obra maestra y al usuario).
     * @return 201 Created confirmando el éxito.
     */
    @PostMapping
    public ResponseEntity<ItemUsuario> createItem(@RequestBody ItemUsuario itemUsuario) {
        ItemUsuario saved = itemUsuarioService.save(itemUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Actualiza el estado, progreso, o valoración de un ítem existente (ej. pasar de "Plan to Watch" a "Completed").
     * @param id El identificador del registro en biblioteca.
     * @param itemUsuario Los nuevos datos enviados por el frontend.
     * @return 200 OK con el ítem modificado, o 404 si no existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemUsuario> updateItem(@PathVariable Integer id, @RequestBody ItemUsuario itemUsuario) {
        if (!itemUsuarioService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        itemUsuario.setIdItemUsuario(id);
        return ResponseEntity.ok(itemUsuarioService.save(itemUsuario));
    }

    /**
     * Elimina un ítem de la biblioteca de un usuario.
     * @param id El ID interno del registro a borrar.
     * @return 204 No Content confirmando la acción, o 404 si ya no estaba en la base de datos.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
        if (!itemUsuarioService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        itemUsuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

