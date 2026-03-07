package com.proyecto.keeply.controllers;

import com.proyecto.keeply.dto.ResultadoBusquedaDTO;
import com.proyecto.keeply.entities.TipoObra;
import com.proyecto.keeply.services.GoogleBooksService;
import com.proyecto.keeply.services.JikanService;
import com.proyecto.keeply.services.RawgService;
import com.proyecto.keeply.services.TmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/busqueda")
@RequiredArgsConstructor
public class BusquedaController {

    private final TmdbService tmdbService;
    private final JikanService jikanService;
    private final RawgService rawgService;
    private final GoogleBooksService googleBooksService;

    /**
     * Buscar películas en TMDB
     */
    @GetMapping("/tmdb/peliculas")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarPeliculas(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = tmdbService.buscarPeliculas(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Buscar series en TMDB
     */
    @GetMapping("/tmdb/series")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarSeries(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = tmdbService.buscarSeries(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Buscar animes en Jikan
     */
    @GetMapping("/jikan/animes")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarAnimes(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = jikanService.buscarAnimes(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Buscar mangas en Jikan
     */
    @GetMapping("/jikan/mangas")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarMangas(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = jikanService.buscarMangas(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Buscar videojuegos en RAWG
     */
    @GetMapping("/rawg/videojuegos")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarVideojuegos(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = rawgService.buscarVideojuegos(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Buscar libros en Google Books
     */
    @GetMapping("/google/books")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarLibros(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = googleBooksService.buscarLibros(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Buscar cómics en Google Books
     */
    @GetMapping("/google/comics")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarComics(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = googleBooksService.buscarComics(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Búsqueda unificada por tipo de obra
     * Permite buscar en todas las APIs relevantes según el tipo
     */
    @GetMapping("/unificada")
    public ResponseEntity<List<ResultadoBusquedaDTO>> busquedaUnificada(
            @RequestParam String query,
            @RequestParam TipoObra tipo) {
        
        List<ResultadoBusquedaDTO> resultados = switch (tipo) {
            case PELICULA -> tmdbService.buscarPeliculas(query);
            case SERIE -> tmdbService.buscarSeries(query);
            case ANIME -> jikanService.buscarAnimes(query);
            case MANGA -> jikanService.buscarMangas(query);
            case VIDEOJUEGO -> rawgService.buscarVideojuegos(query);
            case LIBRO -> googleBooksService.buscarLibros(query);
            case COMIC -> googleBooksService.buscarComics(query);
        };
        
        return ResponseEntity.ok(resultados);
    }
}


