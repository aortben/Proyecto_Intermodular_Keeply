package com.proyecto.keeply.entities;

/**
 * Determina si una Obra fue extraída de una API externa (TMDB, RAWG) 
 * o si fue introducida a mano en el sistema por un administrador o usuario.
 */
public enum OrigenDatos {
    MANUAL, API
}
