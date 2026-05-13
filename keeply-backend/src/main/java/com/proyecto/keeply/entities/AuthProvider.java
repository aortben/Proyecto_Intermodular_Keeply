package com.proyecto.keeply.entities;

/**
 * Indica si el usuario se registró con su email y contraseña tradicionales (LOCAL)
 * o si utilizó el inicio de sesión rápido (SSO) a través de Google.
 */
public enum AuthProvider {
    LOCAL, GOOGLE
}
