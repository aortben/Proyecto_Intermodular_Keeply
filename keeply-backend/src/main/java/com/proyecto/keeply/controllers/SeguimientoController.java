package com.proyecto.keeply.controllers;

import com.proyecto.keeply.entities.Seguimiento;
import com.proyecto.keeply.services.SeguimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seguimientos")
@RequiredArgsConstructor
public class SeguimientoController {

    private final SeguimientoService seguimientoService;

    @GetMapping
    public ResponseEntity<List<Seguimiento>> getAllSeguimientos() {
        return ResponseEntity.ok(seguimientoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seguimiento> getSeguimientoById(@PathVariable Integer id) {
        return seguimientoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/seguidor/{seguidorId}")
    public ResponseEntity<List<Seguimiento>> getSeguimientosBySeguidor(@PathVariable Integer seguidorId) {
        return ResponseEntity.ok(seguimientoService.findBySeguidorId(seguidorId));
    }

    @GetMapping("/seguido/{seguidoId}")
    public ResponseEntity<List<Seguimiento>> getSeguimientosBySeguido(@PathVariable Integer seguidoId) {
        return ResponseEntity.ok(seguimientoService.findBySeguidoId(seguidoId));
    }

    @PostMapping
    public ResponseEntity<?> createSeguimiento(@RequestBody Seguimiento seguimiento) {
        try {
            Seguimiento saved = seguimientoService.save(seguimiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeguimiento(@PathVariable Integer id) {
        if (!seguimientoService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        seguimientoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

