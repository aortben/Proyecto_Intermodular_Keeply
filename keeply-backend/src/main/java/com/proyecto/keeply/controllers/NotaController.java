package com.proyecto.keeply.controllers;

import com.proyecto.keeply.dto.NotaRequestDTO;
import com.proyecto.keeply.entities.Nota;
import com.proyecto.keeply.services.NotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de la gestión de Notas.
 * Una nota es un comentario o apunte que un usuario realiza sobre un elemento
 * específico de su biblioteca (ItemUsuario).
 */
@RestController
@RequestMapping("/api/notas")
@RequiredArgsConstructor
public class NotaController {

    private final NotaService notaService;

    /**
     * Obtiene todas las notas asociadas a un elemento de la biblioteca,
     * incluyendo sus adjuntos (fotos, archivos), ordenadas por fecha de forma descendente.
     * @param itemUsuarioId ID del ítem en la biblioteca.
     * @return 200 OK con la lista de notas.
     */
    @GetMapping("/item/{itemUsuarioId}")
    public ResponseEntity<List<Nota>> getNotasByItem(@PathVariable Integer itemUsuarioId) {
        return ResponseEntity.ok(notaService.findByItemUsuarioId(itemUsuarioId));
    }

    /**
     * Obtiene los detalles de una nota específica por su ID.
     * @param id ID único de la nota.
     * @return 200 OK si existe, 404 si no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Nota> getNotaById(@PathVariable Integer id) {
        return notaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea una nueva nota. Permite recibir también una lista de adjuntos opcionales
     * (imágenes, audios) para ser guardados en la misma transacción.
     * Body esperado: { idItemUsuario, textoNota, adjuntos: [{ tipoAdjunto, urlArchivo }] }
     * 
     * @param dto El objeto de transferencia de datos con la información de la nota.
     * @return 201 Created con la nota persistida.
     */
    @PostMapping
    public ResponseEntity<Nota> createNota(@RequestBody NotaRequestDTO dto) {
        Nota saved = notaService.crearDesdeDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Elimina permanentemente una nota. Gracias a la configuración en cascada de Hibernate,
     * eliminar la nota también elimina automáticamente sus adjuntos asociados de la base de datos.
     * 
     * @param id ID de la nota a borrar.
     * @return 204 No Content confirmando la acción, o 404 si no existía.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNota(@PathVariable Integer id) {
        if (notaService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        notaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
