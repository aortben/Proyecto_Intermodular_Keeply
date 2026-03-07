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
public class RawgService {
    
    @Value("${api.rawg.key:}")
    private String apiKey;
    
    private final RestTemplate restTemplate;

    /**
     * Busca videojuegos en RAWG API
     * TODO: Implementar llamada real a la API cuando se tenga la clave
     */
    public List<ResultadoBusquedaDTO> buscarVideojuegos(String query) {
        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();
        
        // PLACEHOLDER: Datos de ejemplo
        resultados.add(ResultadoBusquedaDTO.builder()
                .idExterno("3498")
                .titulo("Grand Theft Auto V")
                .descripcion("Un juego de mundo abierto ambientado en Los Santos, una versión satírica de Los Ángeles.")
                .imagenUrl("https://media.rawg.io/media/games/456/456dea5e1c7e3cd07060c14e96612001.jpg")
                .tipoObra(TipoObra.VIDEOJUEGO)
                .autorCreador("Rockstar Games")
                .fechaLanzamiento(LocalDate.of(2013, 9, 17))
                .origenApi("RAWG")
                .build());
        
        resultados.add(ResultadoBusquedaDTO.builder()
                .idExterno("3328")
                .titulo("The Witcher 3: Wild Hunt")
                .descripcion("Geralt de Rivia, un cazador de monstruos mutado, busca a su hija adoptiva en un mundo devastado por la guerra.")
                .imagenUrl("https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10b6bcdbc.jpg")
                .tipoObra(TipoObra.VIDEOJUEGO)
                .autorCreador("CD Projekt RED")
                .fechaLanzamiento(LocalDate.of(2015, 5, 19))
                .origenApi("RAWG")
                .build());
        
        resultados.add(ResultadoBusquedaDTO.builder()
                .idExterno("4200")
                .titulo("Portal 2")
                .descripcion("Chell debe escapar de Aperture Science Laboratories usando un dispositivo que crea portales.")
                .imagenUrl("https://media.rawg.io/media/games/328/3283617cb7d75d67257fc58339188742.jpg")
                .tipoObra(TipoObra.VIDEOJUEGO)
                .autorCreador("Valve")
                .fechaLanzamiento(LocalDate.of(2011, 4, 19))
                .origenApi("RAWG")
                .build());
        
        // TODO: Implementar llamada real
        // String url = "https://api.rawg.io/api/games?key=" + apiKey + "&search=" + query;
        // ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        return resultados;
    }
}


