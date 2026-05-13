package com.proyecto.keeply.services;

import com.proyecto.keeply.dto.ResultadoBusquedaDTO;
import com.proyecto.keeply.entities.TipoObra;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servicio encargado de la integración con RAWG API.
 * Permite buscar la base de datos de videojuegos más grande del mundo.
 */
@Service
public class RawgService {

        // Clave obligatoria pero RAWG permite un límite de peticiones sin ella temporalmente
        private final String apiKey;
        private final RestTemplate restTemplate;

        public RawgService(RestTemplate restTemplate, @Value("${api.rawg.key:}") String apiKey) {
                this.restTemplate = restTemplate;
                this.apiKey = apiKey;
        }

        /**
         * Busca videojuegos en RAWG en base al título.
         * Extrae metadatos como desarrollador, géneros y fecha de salida.
         * @param query Texto a buscar.
         * @return Lista de videojuegos formateados en nuestro DTO general.
         */
        public List<ResultadoBusquedaDTO> buscarVideojuegos(String query) {
                List<ResultadoBusquedaDTO> resultados = new ArrayList<>();

                // Advertencia en la consola del servidor si falta la clave, ya que RAWG puede restringir peticiones
                if (apiKey == null || apiKey.isEmpty()) {
                        System.err.println(
                                        "RAWG: No hay API key configurada. Consigue una gratis en https://rawg.io/apidocs");
                }

                try {
                        // Construcción de la URL base con un límite de 15 juegos por página
                        String url = "https://api.rawg.io/api/games?search=" + query + "&page_size=15";
                        if (apiKey != null && !apiKey.isEmpty()) {
                                url += "&key=" + apiKey;
                        }

                        // Realiza la petición GET
                        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                        // Mapea la lista "results" devuelta por la API
                        if (response != null && response.containsKey("results")) {
                                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("results");
                                
                                for (Map<String, Object> item : items) {
                                        String idStr = item.get("id") != null ? item.get("id").toString() : "";
                                        String titulo = item.get("name") != null ? item.get("name").toString() : "";

                                        // Extrae la imagen principal del juego
                                        String imagenUrl = item.get("background_image") != null
                                                        ? item.get("background_image").toString()
                                                        : null;

                                        // Convierte la fecha de "released" (yyyy-mm-dd) a LocalDate
                                        LocalDate fecha = null;
                                        String releasedStr = item.get("released") != null
                                                        ? item.get("released").toString()
                                                        : null;
                                        if (releasedStr != null && !releasedStr.isEmpty()) {
                                                try {
                                                        fecha = LocalDate.parse(releasedStr);
                                                } catch (Exception e) {
                                                        // Fallo silencioso si la fecha viene mal formada
                                                }
                                        }

                                        // RAWG no siempre devuelve el 'publisher' o 'developer' en la búsqueda general.
                                        // Para suplirlo de forma visual, unimos los nombres de los géneros como 'autor'.
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

                                        // Agrega el juego procesado a la lista
                                        resultados.add(ResultadoBusquedaDTO.builder()
                                                        .idExterno(idStr)
                                                        .titulo(titulo)
                                                        .descripcion("") // RAWG no devuelve la descripción en el endpoint de listado, solo en el detalle
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
