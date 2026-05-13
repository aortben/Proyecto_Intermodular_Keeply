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

/**
 * Servicio encargado de la integración con la API externa de The Movie Database (TMDB).
 * Permite buscar películas y series de televisión para añadirlas a la biblioteca.
 */
@Service
@RequiredArgsConstructor
public class TmdbService {
    
    // Clave de API inyectada desde application.properties (obligatoria para peticiones reales)
    @Value("${api.tmdb.key:}")
    private String apiKey;
    
    private final RestTemplate restTemplate;

    /**
     * Busca películas en la API de TMDB.
     * Si no hay una API Key configurada, devuelve un conjunto de datos ficticios de prueba.
     * @param query El título o palabra clave de la película a buscar.
     * @return Lista de películas adaptadas al DTO de búsqueda.
     */
    public List<ResultadoBusquedaDTO> buscarPeliculas(String query) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();
        
        // Modo "Fallback/Mock" si no hay clave de API configurada
        if (apiKey == null || apiKey.isEmpty()) {
            resultados.add(ResultadoBusquedaDTO.builder()
                    .idExterno("550")
                    .titulo("Fight Club")
                    .descripcion("Un oficinista insomne y un fabricante de jabones forman un club de lucha clandestino que evoluciona hacia algo mucho más grande.")
                    .imagenUrl("https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg")
                    .tipoObra(TipoObra.PELICULA)
                    .autorCreador("David Fincher")
                    .fechaLanzamiento(LocalDate.of(1999, 10, 15))
                    .origenApi("TMDB")
                    .build());
            
            resultados.add(ResultadoBusquedaDTO.builder()
                    .idExterno("278")
                    .titulo("The Shawshank Redemption")
                    .descripcion("Dos hombres encarcelados se unen durante varios años, encontrando consuelo y eventual redención a través de actos de decencia común.")
                    .imagenUrl("https://image.tmdb.org/t/p/w500/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg")
                    .tipoObra(TipoObra.PELICULA)
                    .autorCreador("Frank Darabont")
                    .fechaLanzamiento(LocalDate.of(1994, 9, 23))
                    .origenApi("TMDB")
                    .build());
            
            return resultados;
        }
        
        // Ejecución real contra la API de TMDB
        try {
            // Se utiliza el endpoint de búsqueda de películas, solicitando los datos en español
            String url = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&query=" + query + "&language=es-ES";
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.getForEntity(url, java.util.Map.class);
            java.util.Map<String, Object> body = response.getBody();
            
            if (body != null && body.containsKey("results")) {
                List<java.util.Map<String, Object>> items = (List<java.util.Map<String, Object>>) body.get("results");
                
                for (java.util.Map<String, Object> item : items) {
                    String idStr = item.get("id") != null ? item.get("id").toString() : "";
                    String titulo = item.get("title") != null ? item.get("title").toString() : "";
                    String overview = item.get("overview") != null ? item.get("overview").toString() : "";
                    
                    // TMDB devuelve rutas relativas para las imágenes, hay que concatenar la URL base
                    String poster = item.get("poster_path") != null ? "https://image.tmdb.org/t/p/w500" + item.get("poster_path") : null;
                    
                    // Parseo de la fecha de estreno (formato yyyy-MM-dd)
                    String releaseDateStr = item.get("release_date") != null ? item.get("release_date").toString() : null;
                    LocalDate fecha = null;
                    if (releaseDateStr != null && !releaseDateStr.isEmpty()) {
                        try { fecha = LocalDate.parse(releaseDateStr); } catch(Exception e) { /* Se ignora si falla */ }
                    }
                    
                    resultados.add(ResultadoBusquedaDTO.builder()
                            .idExterno(idStr)
                            .titulo(titulo)
                            .descripcion(overview)
                            .imagenUrl(poster)
                            .tipoObra(TipoObra.PELICULA)
                            .fechaLanzamiento(fecha)
                            .origenApi("TMDB")
                            .build());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al buscar en TMDB (Películas): " + e.getMessage());
        }
        
        return resultados;
    }

    /**
     * Busca series de televisión en la API de TMDB.
     * Si no hay API key, devuelve datos de prueba (Breaking Bad).
     * @param query El título de la serie.
     * @return Lista de series adaptadas al DTO de búsqueda.
     */
    public List<ResultadoBusquedaDTO> buscarSeries(String query) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();
        
        // Modo "Fallback/Mock" si no hay clave de API configurada
        if (apiKey == null || apiKey.isEmpty()) {
            resultados.add(ResultadoBusquedaDTO.builder()
                    .idExterno("1396")
                    .titulo("Breaking Bad")
                    .descripcion("Un profesor de química con cáncer terminal se asocia con un ex-estudiante para asegurar el futuro de su familia al fabricar y vender metanfetamina.")
                    .imagenUrl("https://image.tmdb.org/t/p/w500/ggFHVNu6YYI5L9pCfOacjizRGt.jpg")
                    .tipoObra(TipoObra.SERIE)
                    .autorCreador("Vince Gilligan")
                    .fechaLanzamiento(LocalDate.of(2008, 1, 20))
                    .origenApi("TMDB")
                    .build());
            
            return resultados;
        }
        
        // Llamada real a la API de series
        try {
            // Endpoint específico para buscar shows de TV
            String url = "https://api.themoviedb.org/3/search/tv?api_key=" + apiKey + "&query=" + query + "&language=es-ES";
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.getForEntity(url, java.util.Map.class);
            java.util.Map<String, Object> body = response.getBody();
            
            if (body != null && body.containsKey("results")) {
                List<java.util.Map<String, Object>> items = (List<java.util.Map<String, Object>>) body.get("results");
                
                for (java.util.Map<String, Object> item : items) {
                    String idStr = item.get("id") != null ? item.get("id").toString() : "";
                    // En TV Shows la clave es 'name' en lugar de 'title'
                    String titulo = item.get("name") != null ? item.get("name").toString() : "";
                    String overview = item.get("overview") != null ? item.get("overview").toString() : "";
                    
                    String poster = item.get("poster_path") != null ? "https://image.tmdb.org/t/p/w500" + item.get("poster_path") : null;
                    
                    // La fecha de estreno de serie viene en 'first_air_date'
                    String firstAirDateStr = item.get("first_air_date") != null ? item.get("first_air_date").toString() : null;
                    LocalDate fecha = null;
                    if (firstAirDateStr != null && !firstAirDateStr.isEmpty()) {
                        try { fecha = LocalDate.parse(firstAirDateStr); } catch(Exception e) { /* Se ignora */ }
                    }
                    
                    resultados.add(ResultadoBusquedaDTO.builder()
                            .idExterno(idStr)
                            .titulo(titulo)
                            .descripcion(overview)
                            .imagenUrl(poster)
                            .tipoObra(TipoObra.SERIE)
                            .fechaLanzamiento(fecha)
                            .origenApi("TMDB")
                            .build());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al buscar en TMDB (Series): " + e.getMessage());
        }
        return resultados;
    }
}


