package com.proyecto.keeply.config;

import com.proyecto.keeply.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración principal de seguridad de la aplicación usando Spring Security.
 * Define qué rutas son públicas, cuáles requieren autenticación y cómo se procesan los tokens JWT.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * @param http Objeto HttpSecurity para definir las reglas de acceso.
     * @return La cadena de filtros construida.
     * @throws Exception Si ocurre algún error en la configuración.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desactiva CSRF ya que usamos tokens JWT en lugar de cookies de sesión tradicionales
                .csrf(csrf -> csrf.disable())
                // Habilita la configuración CORS definida en CorsConfig.java
                .cors(Customizer.withDefaults())
                // Configura la aplicación para no guardar estado de sesión (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Reglas de autorización de rutas
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas que no requieren estar logueado
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll() // Registro de usuarios
                        .requestMatchers(HttpMethod.GET, "/api/busqueda/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/obras/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/archivos/**").permitAll() // Ver imágenes subidas
                        .requestMatchers("/error").permitAll()
                        // Recursos estáticos del frontend empaquetado (Angular)
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/*.js", "/*.css").permitAll()
                        // Cualquier otra petición requiere un token válido
                        .anyRequest().authenticated())
                // Configura el proveedor de autenticación que buscará al usuario en la BD
                .authenticationProvider(authenticationProvider())
                // Añade nuestro filtro JWT antes del filtro estándar de Spring Security
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Proveedor de autenticación que enlaza nuestro servicio de usuarios con Spring Security.
     * @return El proveedor configurado con el encriptador de contraseñas.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Expone el AuthenticationManager global de Spring para poder inyectarlo en nuestros controladores.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Define el algoritmo de encriptación para las contraseñas.
     * Usamos BCrypt, que es el estándar recomendado para Spring Security.
     * @return Instancia de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
