package com.proyecto.keeply.controllers;

import com.proyecto.keeply.entities.Obra;
import com.proyecto.keeply.entities.TipoObra;
import com.proyecto.keeply.services.ObraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el catálogo general de Obras (Entidades base de entretenimiento).
 * Funciona como el directorio interno de todo lo que los usuarios han ido buscando y añadiendo.
 */
@RestController
@RequestMapping("/api/obras")
@RequiredArgsConstructor
public class ObraController {

    private final ObraService obraService;

    /**
     * Devuelve la lista de obras registradas en el sistema.
     * Soporta filtros opcionales por Tipo (Libro, Serie, etc.) o Título.
     * @param tipo (Opcional) Filtrar por tipo de obra.
     * @param titulo (Opcional) Filtrar por fragmento del título.
     * @return 200 OK con los resultados filtrados o completos.
     */
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

    /**
     * Obtiene una obra específica de la base de datos por su ID.
     * @param id Identificador de la obra.
     * @return 200 OK con la obra, o 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Obra> getObraById(@PathVariable Integer id) {
        return obraService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea manualmente una nueva obra en la base de datos.
     * Usado internamente antes de vincularla a un usuario si la obra es descubierta por primera vez.
     * @param obra Objeto JSON de la obra.
     * @return 201 Created con la nueva obra y su ID.
     */
    @PostMapping
    public ResponseEntity<Obra> createObra(@RequestBody Obra obra) {
        Obra saved = obraService.save(obra);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Modifica de manera completa una obra existente en el sistema.
     * @param id ID de la obra a editar.
     * @param obra Objeto con los nuevos datos.
     * @return 200 OK con la obra actualizada o 404 si el ID no corresponde.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Obra> updateObra(@PathVariable Integer id, @RequestBody Obra obra) {
        if (!obraService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        obra.setIdObra(id);
        return ResponseEntity.ok(obraService.save(obra));
    }

    /**
     * Elimina una obra de la base de datos.
     * ATENCIÓN: Si la obra está vinculada a bibliotecas de usuarios, 
     * podría fallar por llaves foráneas dependiendo de la política en BD.
     * @param id ID de la obra.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObra(@PathVariable Integer id) {
        if (!obraService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        obraService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
