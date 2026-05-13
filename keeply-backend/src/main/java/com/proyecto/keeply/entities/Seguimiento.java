package com.proyecto.keeply.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que modela la relación social de seguimiento entre dos usuarios ("Follow").
 * Refleja quién sigue a quién dentro de la plataforma.
 */
@Entity
@Table(name = "Seguimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seguimiento {
    
    // Identificador único de la relación
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSeguimiento;

    // El usuario que inicia la acción ("El Fan" / "El Seguidor")
    @ManyToOne
    @JoinColumn(name = "id_usuario_seguidor", nullable = false)
    private Usuario seguidor;

    // El usuario que recibe la acción ("El Ídolo" / "El Seguido")
    @ManyToOne
    @JoinColumn(name = "id_usuario_seguido", nullable = false)
    private Usuario seguido;

    // Cuándo ocurrió el "Follow"
    @Column(name = "fecha_seguimiento", updatable = false)
    private LocalDateTime fechaSeguimiento;

    /**
     * Ciclo de vida JPA: Graba la fecha exacta de forma automática al hacer follow.
     */
    @PrePersist
    protected void onCreate() {
        fechaSeguimiento = LocalDateTime.now();
    }
}
