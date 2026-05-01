package com.proyecto.keeply.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Nota")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNota;

    @ManyToOne
    @JoinColumn(name = "id_item_usuario", nullable = false)
    private ItemUsuario itemUsuario;

    @Column(columnDefinition = "TEXT")
    private String textoNota;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<Adjunto> adjuntos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
