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

@Service
@RequiredArgsConstructor
public class JikanService {

    @Value("${api.jikan.key:}")
    private String apiKey;

    private final RestTemplate restTemplate;

    /**
     * Busca animes en Jikan API (no requiere API key)
     */
    public List<ResultadoBusquedaDTO> buscarAnimes(String query) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();

        try {
            String url = "https://api.jikan.moe/v4/anime?q=" + query + "&limit=15";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("data")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("data");
                for (Map<String, Object> item : items) {
                    String idStr = item.get("mal_id") != null ? item.get("mal_id").toString() : "";

                    // Título: primero en español, luego en inglés, luego original
                    String titulo = "";
                    if (item.get("title_english") != null && !item.get("title_english").toString().isEmpty()) {
                        titulo = item.get("title_english").toString();
                    } else if (item.get("title") != null) {
                        titulo = item.get("title").toString();
                    }

                    String sinopsis = item.get("synopsis") != null ? item.get("synopsis").toString() : "";

                    // Imagen
                    String imagenUrl = null;
                    if (item.get("images") != null) {
                        Map<String, Object> images = (Map<String, Object>) item.get("images");
                        if (images.get("jpg") != null) {
                            Map<String, Object> jpg = (Map<String, Object>) images.get("jpg");
                            imagenUrl = jpg.get("large_image_url") != null ? jpg.get("large_image_url").toString()
                                    : null;
                        }
                    }

                    // Fecha
                    LocalDate fecha = null;
                    if (item.get("aired") != null) {
                        Map<String, Object> aired = (Map<String, Object>) item.get("aired");
                        String from = aired.get("from") != null ? aired.get("from").toString() : null;
                        if (from != null && from.length() >= 10) {
                            try {
                                fecha = LocalDate.parse(from.substring(0, 10));
                            } catch (Exception e) {
                            }
                        }
                    }

                    // Estudio/Autor
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
     * Busca mangas en Jikan API (no requiere API key)
     */
    public List<ResultadoBusquedaDTO> buscarMangas(String query) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();

        try {
            String url = "https://api.jikan.moe/v4/manga?q=" + query + "&limit=15";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("data")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("data");
                for (Map<String, Object> item : items) {
                    String idStr = item.get("mal_id") != null ? item.get("mal_id").toString() : "";

                    String titulo = "";
                    if (item.get("title_english") != null && !item.get("title_english").toString().isEmpty()) {
                        titulo = item.get("title_english").toString();
                    } else if (item.get("title") != null) {
                        titulo = item.get("title").toString();
                    }

                    String sinopsis = item.get("synopsis") != null ? item.get("synopsis").toString() : "";

                    String imagenUrl = null;
                    if (item.get("images") != null) {
                        Map<String, Object> images = (Map<String, Object>) item.get("images");
                        if (images.get("jpg") != null) {
                            Map<String, Object> jpg = (Map<String, Object>) images.get("jpg");
                            imagenUrl = jpg.get("large_image_url") != null ? jpg.get("large_image_url").toString()
                                    : null;
                        }
                    }

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
