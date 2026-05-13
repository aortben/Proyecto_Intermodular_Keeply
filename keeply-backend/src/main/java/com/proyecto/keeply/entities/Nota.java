package com.proyecto.keeply.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que modela un comentario, diario o nota textual que el usuario escribe
 * sobre un elemento de su biblioteca. Puede contener archivos multimedia adjuntos.
 */
@Entity
@Table(name = "Nota")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nota {
    
    // Identificador único de la nota
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNota;

    // Referencia al ítem de biblioteca sobre el cual se está tomando la nota
    @ManyToOne
    @JoinColumn(name = "id_item_usuario", nullable = false)
    private ItemUsuario itemUsuario;

    // El cuerpo del comentario / texto rico
    @Column(columnDefinition = "TEXT")
    private String textoNota;

    // Momento exacto de su creación
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    // Colección de archivos (imágenes, audios) que fueron adjuntados a esta nota específica.
    // 'orphanRemoval = true' asegura que si se quita el adjunto de la lista, se elimina de la base de datos.
    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<Adjunto> adjuntos = new ArrayList<>();

    /**
     * Ciclo de vida JPA: Fecha de creación automática al insertar.
     */
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
