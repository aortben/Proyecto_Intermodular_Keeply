package com.proyecto.keeply.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa la relación (Muchos a Muchos desglosada) entre un Usuario y una Obra.
 * Almacena el estado personal, progreso y valoración que un usuario le da a un ítem en su biblioteca.
 */
@Entity
@Table(name = "Item_Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemUsuario {
    
    // Identificador único de esta entrada en la biblioteca
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idItemUsuario;

    // Usuario dueño de la biblioteca donde reside el ítem
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Obra maestra a la que hace referencia este ítem
    @ManyToOne
    @JoinColumn(name = "id_obra", nullable = false)
    private Obra obra;

    // Estado en el que se encuentra (Pendiente, EnProgreso, Terminado, etc)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoItem estado = EstadoItem.Pendiente;

    // Puntuación del 1 al 10 que el usuario le otorga (puede incluir decimales)
    @Column(precision = 3, scale = 1)
    private BigDecimal valoracionPersonal;

    // Cuándo fue agregado a la biblioteca
    @Column(name = "fecha_adicion", updatable = false)
    private LocalDateTime fechaAdicion;

    /**
     * Ciclo de vida JPA: Fija la fecha exacta en la que el usuario lo añade a su lista
     */
    @PrePersist
    protected void onCreate() {
        fechaAdicion = LocalDateTime.now();
    }
}
