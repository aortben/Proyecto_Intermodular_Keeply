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
public class RawgService {

        @Value("${api.rawg.key:}")
        private String apiKey;

        private final RestTemplate restTemplate;

        /**
         * Busca videojuegos en RAWG API
         * RAWG requiere una API key gratuita: https://rawg.io/apidocs
         * Si no hay key, devuelve datos de ejemplo
         */
        public List<ResultadoBusquedaDTO> buscarVideojuegos(String query) {
                List<ResultadoBusquedaDTO> resultados = new ArrayList<>();

                if (apiKey == null || apiKey.isEmpty()) {
                        // Sin API key: intentar sin ella (RAWG puede funcionar sin key para pocas
                        // peticiones)
                        System.err.println(
                                        "RAWG: No hay API key configurada. Consigue una gratis en https://rawg.io/apidocs");
                }

                try {
                        String url = "https://api.rawg.io/api/games?search=" + query + "&page_size=15";
                        if (apiKey != null && !apiKey.isEmpty()) {
                                url += "&key=" + apiKey;
                        }

                        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                        if (response != null && response.containsKey("results")) {
                                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("results");
                                for (Map<String, Object> item : items) {
                                        String idStr = item.get("id") != null ? item.get("id").toString() : "";
                                        String titulo = item.get("name") != null ? item.get("name").toString() : "";

                                        // Imagen
                                        String imagenUrl = item.get("background_image") != null
                                                        ? item.get("background_image").toString()
                                                        : null;

                                        // Fecha
                                        LocalDate fecha = null;
                                        String releasedStr = item.get("released") != null
                                                        ? item.get("released").toString()
                                                        : null;
                                        if (releasedStr != null && !releasedStr.isEmpty()) {
                                                try {
                                                        fecha = LocalDate.parse(releasedStr);
                                                } catch (Exception e) {
                                                }
                                        }

                                        // Desarrolladores/Publicadores (no siempre en search results)
                                        String autor = "";
                                        if (item.get("genres") != null) {
                                                List<Map<String, Object>> genres = (List<Map<String, Object>>) item
                                                                .get("genres");
                                                List<String> genreNames = new ArrayList<>();
                                                for (Map<String, Object> genre : genres) {
                                                        if (genre.get("name") != null)
                                                                genreNames.add(genre.get("name").toString());
                                                }
                                                autor = String.join(", ", genreNames);
                                        }

                                        resultados.add(ResultadoBusquedaDTO.builder()
                                                        .idExterno(idStr)
                                                        .titulo(titulo)
                                                        .descripcion("") // RAWG no devuelve descripción en el listado
                                                        .imagenUrl(imagenUrl)
                                                        .tipoObra(TipoObra.VIDEOJUEGO)
                                                        .autorCreador(autor)
                                                        .fechaLanzamiento(fecha)
                                                        .origenApi("RAWG")
                                                        .build());
                                }
                        }
                } catch (Exception e) {
                        System.err.println("Error al buscar videojuegos en RAWG: " + e.getMessage());
                }

                return resultados;
        }
}
