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
 * Servicio encargado de la integración con la API externa de Google Books.
 * Permite buscar libros y cómics, mapeando las respuestas al formato DTO de Keeply.
 */
@Service
@RequiredArgsConstructor
public class GoogleBooksService {

    // Clave de API inyectada desde application.properties. Es opcional para esta API específica.
    @Value("${api.google.books.key:}")
    private String apiKey;

    // Cliente HTTP para realizar las peticiones externas
    private final RestTemplate restTemplate;

    /**
     * Busca libros en la API de Google Books basándose en una consulta de texto.
     * @param query Texto a buscar (título, autor, ISBN...).
     * @return Lista de libros formateados como ResultadoBusquedaDTO.
     */
    public List<ResultadoBusquedaDTO> buscarLibros(String query) {
        return buscar(query, TipoObra.LIBRO, "");
    }

    /**
     * Busca cómics en la API de Google Books. 
     * Añade un filtro especial ("+subject:comics") para mejorar la precisión.
     * @param query Texto a buscar.
     * @return Lista de cómics formateados como ResultadoBusquedaDTO.
     */
    public List<ResultadoBusquedaDTO> buscarComics(String query) {
        return buscar(query, TipoObra.COMIC, "+subject:comics");
    }

    /**
     * Método privado principal que realiza la petición HTTP y parsea el JSON resultante.
     * @param query El texto buscado por el usuario.
     * @param tipo El tipo de obra (LIBRO o COMIC) para mapearlo correctamente en el DTO.
     * @param extra Parámetros adicionales para la URL de la API (ej. filtros de materia).
     * @return Lista de resultados ya adaptados a nuestro sistema.
     */
    private List<ResultadoBusquedaDTO> buscar(String query, TipoObra tipo, String extra) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();

        try {
            // Construcción de la URL base con límite de 15 resultados y restricción de idioma a español
            String url = "https://www.googleapis.com/books/v1/volumes?q=" + query + extra
                    + "&maxResults=15&langRestrict=es";
                    
            // Añade la API key si está configurada en las propiedades
            if (apiKey != null && !apiKey.isEmpty()) {
                url += "&key=" + apiKey;
            }

            // Realiza la petición GET a Google Books
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // Si hay respuesta y contiene el array "items", procedemos a parsear cada elemento
            if (response != null && response.containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
                
                for (Map<String, Object> item : items) {
                    String idStr = item.get("id") != null ? item.get("id").toString() : "";

                    Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");
                    if (volumeInfo == null)
                        continue; // Si no hay información del volumen, saltamos este item

                    String titulo = volumeInfo.get("title") != null ? volumeInfo.get("title").toString() : "";
                    String descripcion = volumeInfo.get("description") != null
                            ? volumeInfo.get("description").toString()
                            : "";

                    // Extracción de la URL de la imagen de portada
                    String imagenUrl = null;
                    if (volumeInfo.get("imageLinks") != null) {
                        Map<String, Object> imageLinks = (Map<String, Object>) volumeInfo.get("imageLinks");
                        if (imageLinks.get("thumbnail") != null) {
                            // Reemplazamos http por https para evitar bloqueos de contenido mixto en el frontend
                            imagenUrl = imageLinks.get("thumbnail").toString().replace("http://", "https://");
                        }
                    }

                    // Extracción y formateo de la lista de autores
                    String autor = "";
                    if (volumeInfo.get("authors") != null) {
                        List<String> authors = (List<String>) volumeInfo.get("authors");
                        if (!authors.isEmpty()) {
                            autor = String.join(", ", authors);
                        }
                    }

                    // Parseo de la fecha de publicación (Google a veces devuelve solo el año, el año y mes, o fecha completa)
                    LocalDate fecha = null;
                    String publishedDate = volumeInfo.get("publishedDate") != null
                            ? volumeInfo.get("publishedDate").toString()
                            : null;
                            
                    if (publishedDate != null) {
                        try {
                            if (publishedDate.length() == 4) {
                                // Solo año (ej. "2021") -> 1 de enero de 2021
                                fecha = LocalDate.of(Integer.parseInt(publishedDate), 1, 1);
                            } else if (publishedDate.length() == 7) {
                                // Año y mes (ej. "2021-05") -> 1 de mayo de 2021
                                fecha = LocalDate.parse(publishedDate + "-01");
                            } else {
                                // Fecha completa (ej. "2021-05-15")
                                fecha = LocalDate.parse(publishedDate);
                            }
                        } catch (Exception e) {
                            // Ignoramos errores de parseo de fecha y la dejamos en null
                        }
                    }

                    // Construimos y añadimos el DTO a la lista de resultados
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
            // Log de error en caso de fallo de conexión o parseo masivo
            System.err.println("Error al buscar en Google Books: " + e.getMessage());
        }

        return resultados;
    }
}
