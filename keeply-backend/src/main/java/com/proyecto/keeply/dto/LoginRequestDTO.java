package com.proyecto.keeply.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String nombreUsuario;
    private String contrasena;
}
