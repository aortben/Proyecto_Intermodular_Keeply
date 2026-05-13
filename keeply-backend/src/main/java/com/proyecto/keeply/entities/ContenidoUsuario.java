package com.proyecto.keeply.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad (Posiblemente deprecada a favor de 'Nota') que representaba 
 * cualquier tipo de contenido o reseña adicional que el usuario asocia a su ítem.
 * Se mantiene por retrocompatibilidad o flujos alternativos.
 */
@Entity
@Table(name = "Contenido_Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenidoUsuario {
    
    // Identificador único
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idContenido;

    // A qué elemento de la biblioteca de qué usuario pertenece
    @ManyToOne
    @JoinColumn(name = "id_item_usuario", nullable = false)
    private ItemUsuario itemUsuario;

    // Si es una reseña, un comentario rápido, etc.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoContenido tipoContenido;

    // Texto escrito por el usuario
    @Column(columnDefinition = "TEXT")
    private String textoNota;

    // Opcional, si hay un archivo vinculado
    private String urlArchivo;

    // Momento en el que se generó este contenido
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Ciclo de vida JPA: Timestamp automático antes de guardar.
     */
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
