package com.proyecto.keeply.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Adjunto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adjunto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAdjunto;

    @ManyToOne
    @JoinColumn(name = "id_nota", nullable = false)
    @JsonBackReference
    private Nota nota;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAdjunto tipoAdjunto;

    @Column(nullable = false)
    private String urlArchivo;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
