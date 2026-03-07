package com.proyecto.keeply.controllers;

import com.proyecto.keeply.entities.ItemUsuario;
import com.proyecto.keeply.services.ItemUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemUsuarioController {

    private final ItemUsuarioService itemUsuarioService;

    @GetMapping
    public ResponseEntity<List<ItemUsuario>> getAllItems() {
        return ResponseEntity.ok(itemUsuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemUsuario> getItemById(@PathVariable Integer id) {
        return itemUsuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ItemUsuario>> getItemsByUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(itemUsuarioService.findByUsuarioId(usuarioId));
    }

    @PostMapping
    public ResponseEntity<ItemUsuario> createItem(@RequestBody ItemUsuario itemUsuario) {
        ItemUsuario saved = itemUsuarioService.save(itemUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemUsuario> updateItem(@PathVariable Integer id, @RequestBody ItemUsuario itemUsuario) {
        if (!itemUsuarioService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        itemUsuario.setIdItemUsuario(id);
        return ResponseEntity.ok(itemUsuarioService.save(itemUsuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
        if (!itemUsuarioService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        itemUsuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

