package com.proyecto.keeply.controllers;

import com.proyecto.keeply.dto.NotaRequestDTO;
import com.proyecto.keeply.entities.Nota;
import com.proyecto.keeply.services.NotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notas")
@RequiredArgsConstructor
public class NotaController {

    private final NotaService notaService;

    /**
     * Obtiene todas las notas de un ítem (con sus adjuntos), ordenadas por fecha
     * desc.
     */
    @GetMapping("/item/{itemUsuarioId}")
    public ResponseEntity<List<Nota>> getNotasByItem(@PathVariable Integer itemUsuarioId) {
        return ResponseEntity.ok(notaService.findByItemUsuarioId(itemUsuarioId));
    }

    /**
     * Obtiene una nota por su id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Nota> getNotaById(@PathVariable Integer id) {
        return notaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea una nota con adjuntos opcionales.
     * Body esperado: { idItemUsuario, textoNota, adjuntos: [{ tipoAdjunto,
     * urlArchivo }] }
     */
    @PostMapping
    public ResponseEntity<Nota> createNota(@RequestBody NotaRequestDTO dto) {
        Nota saved = notaService.crearDesdeDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Elimina una nota y todos sus adjuntos (cascade).
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
