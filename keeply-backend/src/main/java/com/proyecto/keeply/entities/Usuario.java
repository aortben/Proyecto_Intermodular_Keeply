package com.proyecto.keeply.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad principal que representa a un usuario de la aplicación.
 * Mapea la tabla "Usuario" en la base de datos relacional.
 */
@Entity
@Table(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    // Identificador único autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    // Nombre de usuario público (Alias)
    @Column(nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    // Correo electrónico para notificaciones y login
    @Email
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // Hash de la contraseña (BCrypt). Será nulo o tendrá un valor ficticio si el registro fue vía Google
    @Column
    private String contrasenaHash;

    // Proveedor de identidad usado para el registro (LOCAL o GOOGLE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AuthProvider authProvider = AuthProvider.LOCAL;

    // Fecha en la que el usuario se dio de alta
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    // URL apuntando a la foto de perfil (local o de Google)
    private String avatarUrl;

    // JSON serializado con la configuración visual de los banners en su biblioteca
    @Column(columnDefinition = "TEXT")
    private String customBanners;

    /**
     * Ciclo de vida JPA: Antes de insertar el registro en BD por primera vez, 
     * se autocompleta la fecha de registro.
     */
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
