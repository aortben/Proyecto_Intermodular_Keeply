package com.proyecto.keeply.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombreUsuario;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;
}
