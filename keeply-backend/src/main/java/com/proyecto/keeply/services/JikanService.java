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
public class JikanService {
    
    @Value("${api.jikan.key:}")
    private String apiKey; // Jikan API es gratuita pero puede requerir key para más requests
    
    private final RestTemplate restTemplate;

    /**
     * Busca animes en Jikan API
     * TODO: Implementar llamada real a la API cuando se tenga la clave
     */
    public List<ResultadoBusquedaDTO> buscarAnimes(String query) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();
        
        // PLACEHOLDER: Datos de ejemplo
        resultados.add(ResultadoBusquedaDTO.builder()
                .idExterno("1")
                .titulo("Cowboy Bebop")
                .descripcion("En el año 2071, la humanidad ha colonizado varios planetas y lunas del sistema solar, dejando la Tierra inhabitable...")
                .imagenUrl("https://cdn.myanimelist.net/images/anime/4/19644.jpg")
                .tipoObra(TipoObra.ANIME)
                .autorCreador("Shinichirō Watanabe")
                .fechaLanzamiento(LocalDate.of(1998, 4, 3))
                .origenApi("JIKAN")
                .build());
        
        resultados.add(ResultadoBusquedaDTO.builder()
                .idExterno("1735")
                .titulo("Naruto")
                .descripcion("Naruto Uzumaki es un joven ninja que busca el reconocimiento de sus compañeros y sueña con convertirse en Hokage.")
                .imagenUrl("https://cdn.myanimelist.net/images/anime/13/17405.jpg")
                .tipoObra(TipoObra.ANIME)
                .autorCreador("Masashi Kishimoto")
                .fechaLanzamiento(LocalDate.of(2002, 10, 3))
                .origenApi("JIKAN")
                .build());
        
        // TODO: Implementar llamada real
        // String url = "https://api.jikan.moe/v4/anime?q=" + query;
        // ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        return resultados;
    }

    /**
     * Busca mangas en Jikan API
     * TODO: Implementar llamada real a la API cuando se tenga la clave
     */
    public List<ResultadoBusquedaDTO> buscarMangas(String query) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();
        
        // PLACEHOLDER: Datos de ejemplo
        resultados.add(ResultadoBusquedaDTO.builder()
                .idExterno("1")
                .titulo("One Piece")
                .descripcion("Monkey D. Luffy se embarca en una aventura épica para encontrar el legendario tesoro One Piece y convertirse en el Rey de los Piratas.")
                .imagenUrl("https://cdn.myanimelist.net/images/manga/3/55539.jpg")
                .tipoObra(TipoObra.MANGA)
                .autorCreador("Eiichiro Oda")
                .fechaLanzamiento(LocalDate.of(1997, 7, 22))
                .origenApi("JIKAN")
                .build());
        
        // TODO: Implementar llamada real
        // String url = "https://api.jikan.moe/v4/manga?q=" + query;
        
        return resultados;
    }
}


