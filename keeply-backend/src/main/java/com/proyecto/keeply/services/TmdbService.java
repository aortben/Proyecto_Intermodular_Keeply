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

@Service
@RequiredArgsConstructor
public class TmdbService {
    
    @Value("${api.tmdb.key:}")
    private String apiKey;
    
    private final RestTemplate restTemplate;

    /**
     * Busca películas en TMDB
     * TODO: Implementar llamada real a la API cuando se tenga la clave
     */
    public List<ResultadoBusquedaDTO> buscarPeliculas(String query) {
        // PLACEHOLDER: Simular respuesta de la API
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();
        
        if (apiKey == null || apiKey.isEmpty()) {
            // Retornar datos de ejemplo cuando no hay API key
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
        
        // Implementar llamada real a la API
        try {
            String url = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&query=" + query + "&language=es-ES";
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.getForEntity(url, java.util.Map.class);
            java.util.Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("results")) {
                List<java.util.Map<String, Object>> items = (List<java.util.Map<String, Object>>) body.get("results");
                for (java.util.Map<String, Object> item : items) {
                    String idStr = item.get("id") != null ? item.get("id").toString() : "";
                    String titulo = item.get("title") != null ? item.get("title").toString() : "";
                    String overview = item.get("overview") != null ? item.get("overview").toString() : "";
                    String poster = item.get("poster_path") != null ? "https://image.tmdb.org/t/p/w500" + item.get("poster_path") : null;
                    String releaseDateStr = item.get("release_date") != null ? item.get("release_date").toString() : null;
                    LocalDate fecha = null;
                    if (releaseDateStr != null && !releaseDateStr.isEmpty()) {
                        try { fecha = LocalDate.parse(releaseDateStr); } catch(Exception e) {}
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
            System.err.println("Error al buscar en TMDB (Peliculas): " + e.getMessage());
        }
        
        return resultados;
    }

    /**
     * Busca series en TMDB
     * TODO: Implementar llamada real a la API cuando se tenga la clave
     */
    public List<ResultadoBusquedaDTO> buscarSeries(String query) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();
        
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
        
        // Implementar llamada real
        try {
            String url = "https://api.themoviedb.org/3/search/tv?api_key=" + apiKey + "&query=" + query + "&language=es-ES";
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.getForEntity(url, java.util.Map.class);
            java.util.Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("results")) {
                List<java.util.Map<String, Object>> items = (List<java.util.Map<String, Object>>) body.get("results");
                for (java.util.Map<String, Object> item : items) {
                    String idStr = item.get("id") != null ? item.get("id").toString() : "";
                    String titulo = item.get("name") != null ? item.get("name").toString() : "";
                    String overview = item.get("overview") != null ? item.get("overview").toString() : "";
                    String poster = item.get("poster_path") != null ? "https://image.tmdb.org/t/p/w500" + item.get("poster_path") : null;
                    String firstAirDateStr = item.get("first_air_date") != null ? item.get("first_air_date").toString() : null;
                    LocalDate fecha = null;
                    if (firstAirDateStr != null && !firstAirDateStr.isEmpty()) {
                        try { fecha = LocalDate.parse(firstAirDateStr); } catch(Exception e) {}
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


