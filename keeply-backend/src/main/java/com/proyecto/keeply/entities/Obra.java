package com.proyecto.keeply.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Entidad que representa el concepto universal de una Obra (Película, Libro, Anime, etc).
 * Se alimenta de las APIs externas y funciona como catálogo central en la BD de Keeply.
 */
@Entity
@Table(name = "Obra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Obra {
    
    // ID autonumérico interno de Keeply
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idObra;

    // Título de la obra
    @Column(nullable = false)
    private String titulo;

    // Sinopsis o descripción larga
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Clasificación general (Libro, Serie, Videojuego...)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoObra tipoObra;

    // Director, Autor, Estudio o Desarrolladora principal
    private String autorCreador;

    // Fecha en la que la obra se lanzó al público original
    private LocalDate fechaLanzamiento;

    // Enlace HTTP hacia la carátula o póster
    private String urlImagenPrincipal;

    // ID original proporcionado por la API externa (por ejemplo el ID de TMDB o de RAWG)
    private String idExternoApi;

    // Para guardar datos extras complejos de la API en formato JSON si fuese necesario
    @Column(columnDefinition = "TEXT")
    private String detallesJson;

    // Indica de dónde procedió la información
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrigenDatos origenDatos = OrigenDatos.MANUAL;
}
