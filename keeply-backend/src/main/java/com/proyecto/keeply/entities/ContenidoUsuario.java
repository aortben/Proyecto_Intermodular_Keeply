package com.proyecto.keeply.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Contenido_Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenidoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idContenido;

    @ManyToOne
    @JoinColumn(name = "id_item_usuario", nullable = false)
    private ItemUsuario itemUsuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoContenido tipoContenido;

    @Column(columnDefinition = "TEXT")
    private String textoNota;

    private String urlArchivo;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
