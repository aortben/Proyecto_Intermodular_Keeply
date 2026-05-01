package com.proyecto.keeply.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String contrasenaHash;

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    private String avatarUrl;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
