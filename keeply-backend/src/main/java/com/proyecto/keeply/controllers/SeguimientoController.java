package com.proyecto.keeply.controllers;

import com.proyecto.keeply.entities.Seguimiento;
import com.proyecto.keeply.services.SeguimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado del sistema social de "Followers".
 * Permite listar y gestionar quién sigue a quién en la plataforma.
 */
@RestController
@RequestMapping("/api/seguimientos")
@RequiredArgsConstructor
public class SeguimientoController {

    private final SeguimientoService seguimientoService;

    /**
     * Obtiene el listado global de relaciones de seguimiento de la plataforma.
     * @return 200 OK con la lista general.
     */
    @GetMapping
    public ResponseEntity<List<Seguimiento>> getAllSeguimientos() {
        return ResponseEntity.ok(seguimientoService.findAll());
    }

    /**
     * Busca un registro de seguimiento concreto por su identificador.
     * @param id ID interno de la relación.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Seguimiento> getSeguimientoById(@PathVariable Integer id) {
        return seguimientoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Devuelve a todos los usuarios que la persona (seguidorId) está siguiendo actualmente.
     * @param seguidorId ID del usuario emisor del follow.
     * @return Lista de relaciones.
     */
    @GetMapping("/seguidor/{seguidorId}")
    public ResponseEntity<List<Seguimiento>> getSeguimientosBySeguidor(@PathVariable Integer seguidorId) {
        return ResponseEntity.ok(seguimientoService.findBySeguidorId(seguidorId));
    }

    /**
     * Devuelve a todas las personas que siguen al usuario especificado (sus fans).
     * @param seguidoId ID del usuario receptor del follow.
     * @return Lista de relaciones.
     */
    @GetMapping("/seguido/{seguidoId}")
    public ResponseEntity<List<Seguimiento>> getSeguimientosBySeguido(@PathVariable Integer seguidoId) {
        return ResponseEntity.ok(seguimientoService.findBySeguidoId(seguidoId));
    }

    /**
     * Inicia una nueva relación de seguimiento ("Follow").
     * Captura excepciones de negocio, por ejemplo, intentar seguirse a sí mismo.
     * @param seguimiento Datos del emisor y receptor.
     * @return 201 Created si es válido, 400 Bad Request si rompe una regla de negocio.
     */
    @PostMapping
    public ResponseEntity<?> createSeguimiento(@RequestBody Seguimiento seguimiento) {
        try {
            Seguimiento saved = seguimientoService.save(seguimiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            // Maneja el error para no devolver un 500 del servidor, sino un 400 explicativo.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Destruye una relación de seguimiento ("Unfollow").
     * @param id ID de la relación a borrar.
     * @return 204 No Content confirmando la acción.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeguimiento(@PathVariable Integer id) {
        if (!seguimientoService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        seguimientoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

