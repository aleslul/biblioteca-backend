package com.aleslul.biblioteca.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Preflight de CORS siempre debe pasar
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Login y registro de usuarios son publicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

                        // Operaciones administrativas sensibles: solo ADMINISTRADOR
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/*/rol").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/*").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/logs/**").hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")

                        // Multas: pagar/condonar es sensible (solo ADMINISTRADOR); listar es de consulta
                        .requestMatchers(HttpMethod.PUT, "/api/multas/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/multas/usuario/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/multas").hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")

                        // Circulacion de materiales: BIBLIOTECARIO o ADMINISTRADOR
                        .requestMatchers(HttpMethod.POST, "/api/libros").hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.PUT, "/api/libros/*").hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.DELETE, "/api/libros/*").hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.POST, "/api/prestamos").hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.POST, "/api/devoluciones").hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")

                        // RF9: reportes de circulación, solo personal de biblioteca
                        .requestMatchers("/api/reportes/**").hasAnyRole("ADMINISTRADOR", "BIBLIOTECARIO")

                        // El resto de endpoints solo requiere estar autenticado (cualquier rol)
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(this::manejarNoAutenticado)
                        .accessDeniedHandler(this::manejarAccesoDenegado)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 401: no se envio token o el token es invalido/expirado
    private void manejarNoAutenticado(jakarta.servlet.http.HttpServletRequest request,
                                      jakarta.servlet.http.HttpServletResponse response,
                                      org.springframework.security.core.AuthenticationException ex) throws java.io.IOException {
        escribirError(response, request.getRequestURI(), HttpStatus.UNAUTHORIZED, "No autorizado",
                "Debes iniciar sesion para acceder a este recurso");
    }

    // 403: el usuario esta autenticado pero su rol no tiene permiso
    private void manejarAccesoDenegado(jakarta.servlet.http.HttpServletRequest request,
                                       jakarta.servlet.http.HttpServletResponse response,
                                       org.springframework.security.access.AccessDeniedException ex) throws java.io.IOException {
        escribirError(response, request.getRequestURI(), HttpStatus.FORBIDDEN, "Acceso denegado",
                "Tu rol no tiene permisos para realizar esta accion");
    }

    private void escribirError(jakarta.servlet.http.HttpServletResponse response, String path,
                               HttpStatus status, String error, String mensaje) throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String json = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                LocalDateTime.now(),
                status.value(),
                escaparJson(error),
                escaparJson(mensaje),
                escaparJson(path)
        );
        response.getWriter().write(json);
    }

    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}