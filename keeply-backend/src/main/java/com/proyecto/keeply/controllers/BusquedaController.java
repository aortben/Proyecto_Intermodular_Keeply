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

/**
 * Controlador REST (Endpoint API) encargado de exponer las funcionalidades de búsqueda
 * en las distintas bases de datos externas (TMDB, RAWG, Jikan, Google Books).
 * Actúa como pasarela (Gateway) entre el frontend de Angular y los servicios externos.
 */
@RestController
@RequestMapping("/api/busqueda")
@RequiredArgsConstructor
public class BusquedaController {

    private final TmdbService tmdbService;
    private final JikanService jikanService;
    private final RawgService rawgService;
    private final GoogleBooksService googleBooksService;

    /**
     * Endpoint para buscar películas a través de la API de TMDB.
     * @param query Texto a buscar.
     * @return 200 OK con la lista de resultados estandarizados.
     */
    @GetMapping("/tmdb/peliculas")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarPeliculas(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = tmdbService.buscarPeliculas(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Endpoint para buscar series a través de la API de TMDB.
     * @param query Texto a buscar.
     * @return 200 OK con la lista de series.
     */
    @GetMapping("/tmdb/series")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarSeries(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = tmdbService.buscarSeries(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Endpoint para buscar animes en la base de datos de MyAnimeList vía Jikan.
     * @param query Texto a buscar.
     * @return 200 OK con la lista de animes.
     */
    @GetMapping("/jikan/animes")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarAnimes(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = jikanService.buscarAnimes(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Endpoint para buscar mangas en la base de datos de MyAnimeList vía Jikan.
     * @param query Texto a buscar.
     * @return 200 OK con la lista de mangas.
     */
    @GetMapping("/jikan/mangas")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarMangas(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = jikanService.buscarMangas(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Endpoint para buscar videojuegos utilizando RAWG API.
     * @param query Título del videojuego.
     * @return 200 OK con los juegos encontrados.
     */
    @GetMapping("/rawg/videojuegos")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarVideojuegos(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = rawgService.buscarVideojuegos(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Endpoint para buscar libros utilizando Google Books API.
     * @param query Título o autor del libro.
     * @return 200 OK con la lista de libros.
     */
    @GetMapping("/google/books")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarLibros(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = googleBooksService.buscarLibros(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Endpoint para buscar cómics, apoyándose en un filtro especializado sobre Google Books.
     * @param query Título o autor del cómic.
     * @return 200 OK con la lista de cómics.
     */
    @GetMapping("/google/comics")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarComics(@RequestParam String query) {
        List<ResultadoBusquedaDTO> resultados = googleBooksService.buscarComics(query);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Endpoint comodín o de "Búsqueda Unificada".
     * Centraliza las peticiones de frontend que ya saben qué tipo de obra buscan
     * y las redirige internamente al servicio correspondiente sin que el cliente
     * tenga que llamar a diferentes URLs explícitamente.
     * 
     * @param query El texto introducido por el usuario.
     * @param tipo El enumerado TipoObra (ej. PELICULA, LIBRO, VIDEOJUEGO).
     * @return 200 OK con la respuesta formateada del servicio externo.
     */
    @GetMapping("/unificada")
    public ResponseEntity<List<ResultadoBusquedaDTO>> busquedaUnificada(
            @RequestParam String query,
            @RequestParam TipoObra tipo) {
        
        // Uso de switch expressions de Java modernas para simplificar el ruteo interno
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


