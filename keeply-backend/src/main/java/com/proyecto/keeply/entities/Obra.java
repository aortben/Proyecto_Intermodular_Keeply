package com.proyecto.keeply.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "Obra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Obra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idObra;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoObra tipoObra;

    private String autorCreador;

    private LocalDate fechaLanzamiento;

    private String urlImagenPrincipal;

    private String idExternoApi;

    @Column(columnDefinition = "TEXT")
    private String detallesJson;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrigenDatos origenDatos = OrigenDatos.MANUAL;
}
