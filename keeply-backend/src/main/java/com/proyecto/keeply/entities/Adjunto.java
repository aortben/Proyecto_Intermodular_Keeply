package com.proyecto.keeply.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa un archivo multimedia (imagen, video, etc) 
 * que se ha adjuntado a una 'Nota' de la biblioteca de un usuario.
 */
@Entity
@Table(name = "Adjunto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adjunto {
    
    // Identificador único
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAdjunto;

    // La Nota padre a la que pertenece este archivo
    // @JsonBackReference rompe los bucles infinitos al serializar a JSON
    @ManyToOne
    @JoinColumn(name = "id_nota", nullable = false)
    @JsonBackReference
    private Nota nota;

    // Si es una imagen, un vídeo, un documento...
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAdjunto tipoAdjunto;

    // La ruta o URL de almacenamiento remoto donde reside el fichero físico
    @Column(nullable = false)
    private String urlArchivo;

    // Cuándo se subió el archivo
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Ciclo de vida JPA: Timestamp automático de la subida.
     */
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
