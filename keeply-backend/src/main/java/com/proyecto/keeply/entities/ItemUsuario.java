package com.proyecto.keeply.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Item_Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idItemUsuario;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_obra", nullable = false)
    private Obra obra;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoItem estado = EstadoItem.Pendiente;

    @Column(precision = 3, scale = 1)
    private BigDecimal valoracionPersonal;

    @Column(name = "fecha_adicion", updatable = false)
    private LocalDateTime fechaAdicion;

    @PrePersist
    protected void onCreate() {
        fechaAdicion = LocalDateTime.now();
    }
}
