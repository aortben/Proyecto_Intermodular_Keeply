package com.proyecto.keeply.controllers;

import com.proyecto.keeply.entities.Obra;
import com.proyecto.keeply.entities.TipoObra;
import com.proyecto.keeply.services.ObraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/obras")
@RequiredArgsConstructor
public class ObraController {

    private final ObraService obraService;

    @GetMapping
    public ResponseEntity<List<Obra>> getAllObras(@RequestParam(required = false) TipoObra tipo,
                                                   @RequestParam(required = false) String titulo) {
        if (tipo != null) {
            return ResponseEntity.ok(obraService.findByTipoObra(tipo));
        }
        if (titulo != null && !titulo.isEmpty()) {
            return ResponseEntity.ok(obraService.findByTituloContaining(titulo));
        }
        return ResponseEntity.ok(obraService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Obra> getObraById(@PathVariable Integer id) {
        return obraService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Obra> createObra(@RequestBody Obra obra) {
        Obra saved = obraService.save(obra);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Obra> updateObra(@PathVariable Integer id, @RequestBody Obra obra) {
        if (!obraService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        obra.setIdObra(id);
        return ResponseEntity.ok(obraService.save(obra));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObra(@PathVariable Integer id) {
        if (!obraService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        obraService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

