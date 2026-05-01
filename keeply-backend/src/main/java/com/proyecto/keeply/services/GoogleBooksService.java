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
public class GoogleBooksService {

    @Value("${api.google.books.key:}")
    private String apiKey;

    private final RestTemplate restTemplate;

    /**
     * Busca libros en Google Books API (funciona sin API key)
     */
    public List<ResultadoBusquedaDTO> buscarLibros(String query) {
        return buscar(query, TipoObra.LIBRO, "");
    }

    /**
     * Busca cómics en Google Books API (funciona sin API key)
     */
    public List<ResultadoBusquedaDTO> buscarComics(String query) {
        return buscar(query, TipoObra.COMIC, "+subject:comics");
    }

    private List<ResultadoBusquedaDTO> buscar(String query, TipoObra tipo, String extra) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();

        try {
            String url = "https://www.googleapis.com/books/v1/volumes?q=" + query + extra
                    + "&maxResults=15&langRestrict=es";
            if (apiKey != null && !apiKey.isEmpty()) {
                url += "&key=" + apiKey;
            }

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
                for (Map<String, Object> item : items) {
                    String idStr = item.get("id") != null ? item.get("id").toString() : "";

                    Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");
                    if (volumeInfo == null)
                        continue;

                    String titulo = volumeInfo.get("title") != null ? volumeInfo.get("title").toString() : "";
                    String descripcion = volumeInfo.get("description") != null
                            ? volumeInfo.get("description").toString()
                            : "";

                    // Imagen
                    String imagenUrl = null;
                    if (volumeInfo.get("imageLinks") != null) {
                        Map<String, Object> imageLinks = (Map<String, Object>) volumeInfo.get("imageLinks");
                        if (imageLinks.get("thumbnail") != null) {
                            // Reemplazar http por https para evitar mixed content
                            imagenUrl = imageLinks.get("thumbnail").toString().replace("http://", "https://");
                        }
                    }

                    // Autores
                    String autor = "";
                    if (volumeInfo.get("authors") != null) {
                        List<String> authors = (List<String>) volumeInfo.get("authors");
                        if (!authors.isEmpty()) {
                            autor = String.join(", ", authors);
                        }
                    }

                    // Fecha
                    LocalDate fecha = null;
                    String publishedDate = volumeInfo.get("publishedDate") != null
                            ? volumeInfo.get("publishedDate").toString()
                            : null;
                    if (publishedDate != null) {
                        try {
                            if (publishedDate.length() == 4) {
                                fecha = LocalDate.of(Integer.parseInt(publishedDate), 1, 1);
                            } else if (publishedDate.length() == 7) {
                                fecha = LocalDate.parse(publishedDate + "-01");
                            } else {
                                fecha = LocalDate.parse(publishedDate);
                            }
                        } catch (Exception e) {
                        }
                    }

                    resultados.add(ResultadoBusquedaDTO.builder()
                            .idExterno(idStr)
                            .titulo(titulo)
                            .descripcion(descripcion)
                            .imagenUrl(imagenUrl)
                            .tipoObra(tipo)
                            .autorCreador(autor)
                            .fechaLanzamiento(fecha)
                            .origenApi("GOOGLE_BOOKS")
                            .build());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al buscar en Google Books: " + e.getMessage());
        }

        return resultados;
    }
}
