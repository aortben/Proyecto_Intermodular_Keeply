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
public class GoogleBooksService {
    
    @Value("${api.google.books.key:}")
    private String apiKey; // Google Books API puede funcionar sin key pero es mejor tenerla
    
    private final RestTemplate restTemplate;

    /**
     * Busca libros en Google Books API
     * TODO: Implementar llamada real a la API cuando se tenga la clave
     */
    public List<ResultadoBusquedaDTO> buscarLibros(String query) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();
        
        // PLACEHOLDER: Datos de ejemplo
        resultados.add(ResultadoBusquedaDTO.builder()
                .idExterno("ISBN9788498382543")
                .titulo("Cien años de soledad")
                .descripcion("La historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo.")
                .imagenUrl("https://books.google.com/books/content?id=example")
                .tipoObra(TipoObra.LIBRO)
                .autorCreador("Gabriel García Márquez")
                .fechaLanzamiento(LocalDate.of(1967, 6, 1))
                .origenApi("GOOGLE_BOOKS")
                .build());
        
        resultados.add(ResultadoBusquedaDTO.builder()
                .idExterno("ISBN9788490628784")
                .titulo("1984")
                .descripcion("Una distopía sobre un futuro totalitario donde el gobierno controla cada aspecto de la vida.")
                .imagenUrl("https://books.google.com/books/content?id=example2")
                .tipoObra(TipoObra.LIBRO)
                .autorCreador("George Orwell")
                .fechaLanzamiento(LocalDate.of(1949, 6, 8))
                .origenApi("GOOGLE_BOOKS")
                .build());
        
        resultados.add(ResultadoBusquedaDTO.builder()
                .idExterno("ISBN9788420431834")
                .titulo("El Quijote")
                .descripcion("Las aventuras de un hidalgo que se vuelve loco por leer demasiados libros de caballería.")
                .imagenUrl("https://books.google.com/books/content?id=example3")
                .tipoObra(TipoObra.LIBRO)
                .autorCreador("Miguel de Cervantes")
                .fechaLanzamiento(LocalDate.of(1605, 1, 16))
                .origenApi("GOOGLE_BOOKS")
                .build());
        
        // TODO: Implementar llamada real
        // String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;
        // if (apiKey != null && !apiKey.isEmpty()) {
        //     url += "&key=" + apiKey;
        // }
        // ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        return resultados;
    }

    /**
     * Busca cómics en Google Books API
     * TODO: Implementar llamada real a la API cuando se tenga la clave
     */
    public List<ResultadoBusquedaDTO> buscarComics(String query) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();
        
        // PLACEHOLDER: Datos de ejemplo
        resultados.add(ResultadoBusquedaDTO.builder()
                .idExterno("ISBN9788416597157")
                .titulo("Watchmen")
                .descripcion("Una historia de superhéroes desilusionados en un mundo alternativo de la Guerra Fría.")
                .imagenUrl("https://books.google.com/books/content?id=comic1")
                .tipoObra(TipoObra.COMIC)
                .autorCreador("Alan Moore")
                .fechaLanzamiento(LocalDate.of(1986, 9, 1))
                .origenApi("GOOGLE_BOOKS")
                .build());
        
        // TODO: Implementar llamada real con filtro de cómics
        // String url = "https://www.googleapis.com/books/v1/volumes?q=" + query + "+subject:comics";
        
        return resultados;
    }
}


