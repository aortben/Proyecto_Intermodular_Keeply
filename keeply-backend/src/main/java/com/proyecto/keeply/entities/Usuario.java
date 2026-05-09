package com.proyecto.keeply.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

    @Email
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column
    private String contrasenaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AuthProvider authProvider = AuthProvider.LOCAL;

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    private String avatarUrl;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
