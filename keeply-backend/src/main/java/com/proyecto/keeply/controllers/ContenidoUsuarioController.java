package com.proyecto.keeply.controllers;

import com.proyecto.keeply.entities.ContenidoUsuario;
import com.proyecto.keeply.services.ContenidoUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenidos")
@RequiredArgsConstructor
public class ContenidoUsuarioController {

    private final ContenidoUsuarioService contenidoUsuarioService;

    @GetMapping
    public ResponseEntity<List<ContenidoUsuario>> getAllContenidos() {
        return ResponseEntity.ok(contenidoUsuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContenidoUsuario> getContenidoById(@PathVariable Integer id) {
        return contenidoUsuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/item/{itemUsuarioId}")
    public ResponseEntity<List<ContenidoUsuario>> getContenidosByItem(@PathVariable Integer itemUsuarioId) {
        return ResponseEntity.ok(contenidoUsuarioService.findByItemUsuarioId(itemUsuarioId));
    }

    @PostMapping
    public ResponseEntity<ContenidoUsuario> createContenido(@RequestBody ContenidoUsuario contenidoUsuario) {
        ContenidoUsuario saved = contenidoUsuarioService.save(contenidoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContenidoUsuario> updateContenido(@PathVariable Integer id, 
                                                             @RequestBody ContenidoUsuario contenidoUsuario) {
        if (contenidoUsuarioService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        contenidoUsuario.setIdContenido(id);
        return ResponseEntity.ok(contenidoUsuarioService.save(contenidoUsuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContenido(@PathVariable Integer id) {
        if (contenidoUsuarioService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        contenidoUsuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

