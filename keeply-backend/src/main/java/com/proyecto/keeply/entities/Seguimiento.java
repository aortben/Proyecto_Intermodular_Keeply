package com.proyecto.keeply.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Seguimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seguimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSeguimiento;

    @ManyToOne
    @JoinColumn(name = "id_usuario_seguidor", nullable = false)
    private Usuario seguidor;

    @ManyToOne
    @JoinColumn(name = "id_usuario_seguido", nullable = false)
    private Usuario seguido;

    @Column(name = "fecha_seguimiento", updatable = false)
    private LocalDateTime fechaSeguimiento;

    @PrePersist
    protected void onCreate() {
        fechaSeguimiento = LocalDateTime.now();
    }
}
