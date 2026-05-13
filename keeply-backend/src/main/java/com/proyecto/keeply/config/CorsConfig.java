package com.proyecto.keeply.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración global de CORS (Cross-Origin Resource Sharing).
 * Permite que el frontend (ej. localhost o Vercel) se comunique con esta API sin bloqueos del navegador.
 */
@Configuration
public class CorsConfig {

    /**
     * Define las políticas de CORS para toda la aplicación.
     * @return Fuente de configuración de CORS con los orígenes, métodos y cabeceras permitidas.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Orígenes permitidos (desarrollo local y producción en Vercel)
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "https://proyecto-intermodular-keeply.vercel.app"
        ));
        
        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Cabeceras permitidas en las peticiones
        config.setAllowedHeaders(List.of("*"));
        
        // Cabeceras que el frontend puede leer de las respuestas
        config.setExposedHeaders(List.of("Authorization"));
        
        // Permite el envío de credenciales (cookies, cabeceras de autorización)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración a todas las rutas de la API
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    /**
     * Proporciona un cliente HTTP genérico para hacer peticiones a otras APIs externas (ej. Google Books).
     * @return Instancia de RestTemplate lista para usarse.
     */
    @Bean
    public RestTemplate restTemplate() {
        ClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        return new RestTemplate(factory);
    }
}
