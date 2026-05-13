package com.proyecto.keeply.services;

import com.proyecto.keeply.dto.ResultadoBusquedaDTO;
import com.proyecto.keeply.entities.TipoObra;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servicio encargado de la integración con Jikan API.
 * Jikan es un envoltorio (wrapper) de la base de datos MyAnimeList para obtener información de animes y mangas.
 */
@Service
@RequiredArgsConstructor
public class JikanService {

    // Clave de API inyectada desde application.properties (Jikan no requiere una obligatoriamente, pero se puede configurar)
    @Value("${api.jikan.key:}")
    private String apiKey;

    private final RestTemplate restTemplate;

    /**
     * Busca animes en la API de Jikan.
     * @param query Texto a buscar.
     * @return Lista de animes formateados como ResultadoBusquedaDTO.
     */
    public List<ResultadoBusquedaDTO> buscarAnimes(String query) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();

        try {
            // URL con el endpoint específico de anime, limitando a 15 resultados
            String url = "https://api.jikan.moe/v4/anime?q=" + query + "&limit=15";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // Mapea la lista "data" devuelta por la API
            if (response != null && response.containsKey("data")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("data");
                
                for (Map<String, Object> item : items) {
                    String idStr = item.get("mal_id") != null ? item.get("mal_id").toString() : "";

                    // Lógica de títulos: Priorizamos el título en inglés si está disponible, sino el original (romaji)
                    String titulo = "";
                    if (item.get("title_english") != null && !item.get("title_english").toString().isEmpty()) {
                        titulo = item.get("title_english").toString();
                    } else if (item.get("title") != null) {
                        titulo = item.get("title").toString();
                    }

                    String sinopsis = item.get("synopsis") != null ? item.get("synopsis").toString() : "";

                    // Extrae la URL de la imagen en formato JPG en su tamaño más grande
                    String imagenUrl = null;
                    if (item.get("images") != null) {
                        Map<String, Object> images = (Map<String, Object>) item.get("images");
                        if (images.get("jpg") != null) {
                            Map<String, Object> jpg = (Map<String, Object>) images.get("jpg");
                            imagenUrl = jpg.get("large_image_url") != null ? jpg.get("large_image_url").toString()
                                    : null;
                        }
                    }

                    // Extrae la fecha de estreno del objeto "aired" -> "from" (ISO 8601)
                    LocalDate fecha = null;
                    if (item.get("aired") != null) {
                        Map<String, Object> aired = (Map<String, Object>) item.get("aired");
                        String from = aired.get("from") != null ? aired.get("from").toString() : null;
                        if (from != null && from.length() >= 10) {
                            try {
                                fecha = LocalDate.parse(from.substring(0, 10)); // Solo el yyyy-mm-dd
                            } catch (Exception e) {
                                // Fallo silencioso de fecha
                            }
                        }
                    }

                    // Mapea el primer estudio responsable como el "autorCreador" de la obra
                    String autor = "";
                    if (item.get("studios") != null) {
                        List<Map<String, Object>> studios = (List<Map<String, Object>>) item.get("studios");
                        if (!studios.isEmpty()) {
                            autor = studios.get(0).get("name") != null ? studios.get(0).get("name").toString() : "";
                        }
                    }

                    resultados.add(ResultadoBusquedaDTO.builder()
                            .idExterno(idStr)
                            .titulo(titulo)
                            .descripcion(sinopsis)
                            .imagenUrl(imagenUrl)
                            .tipoObra(TipoObra.ANIME)
                            .autorCreador(autor)
                            .fechaLanzamiento(fecha)
                            .origenApi("JIKAN")
                            .build());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al buscar animes en Jikan: " + e.getMessage());
        }

        return resultados;
    }

    /**
     * Busca mangas en la API de Jikan.
     * @param query Texto a buscar.
     * @return Lista de mangas formateados como ResultadoBusquedaDTO.
     */
    public List<ResultadoBusquedaDTO> buscarMangas(String query) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();

        try {
            // URL con el endpoint específico de manga
            String url = "https://api.jikan.moe/v4/manga?q=" + query + "&limit=15";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("data")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("data");
                
                for (Map<String, Object> item : items) {
                    String idStr = item.get("mal_id") != null ? item.get("mal_id").toString() : "";

                    // Lógica de títulos similar al anime
                    String titulo = "";
                    if (item.get("title_english") != null && !item.get("title_english").toString().isEmpty()) {
                        titulo = item.get("title_english").toString();
                    } else if (item.get("title") != null) {
                        titulo = item.get("title").toString();
                    }

                    String sinopsis = item.get("synopsis") != null ? item.get("synopsis").toString() : "";

                    // Imagen de portada
                    String imagenUrl = null;
                    if (item.get("images") != null) {
                        Map<String, Object> images = (Map<String, Object>) item.get("images");
                        if (images.get("jpg") != null) {
                            Map<String, Object> jpg = (Map<String, Object>) images.get("jpg");
                            imagenUrl = jpg.get("large_image_url") != null ? jpg.get("large_image_url").toString()
                                    : null;
                        }
                    }

                    // Extrae la fecha de publicación original (objeto "published")
                    LocalDate fecha = null;
                    if (item.get("published") != null) {
                        Map<String, Object> published = (Map<String, Object>) item.get("published");
                        String from = published.get("from") != null ? published.get("from").toString() : null;
                        if (from != null && from.length() >= 10) {
                            try {
                                fecha = LocalDate.parse(from.substring(0, 10));
                            } catch (Exception e) {
                            }
                        }
                    }

                    // Mapea el mangaka / autor
                    String autor = "";
                    if (item.get("authors") != null) {
                        List<Map<String, Object>> authors = (List<Map<String, Object>>) item.get("authors");
                        if (!authors.isEmpty()) {
                            autor = authors.get(0).get("name") != null ? authors.get(0).get("name").toString() : "";
                        }
                    }

                    resultados.add(ResultadoBusquedaDTO.builder()
                            .idExterno(idStr)
                            .titulo(titulo)
                            .descripcion(sinopsis)
                            .imagenUrl(imagenUrl)
                            .tipoObra(TipoObra.MANGA)
                            .autorCreador(autor)
                            .fechaLanzamiento(fecha)
                            .origenApi("JIKAN")
                            .build());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al buscar mangas en Jikan: " + e.getMessage());
        }

        return resultados;
    }
}
