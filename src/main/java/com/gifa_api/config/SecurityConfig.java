package com.gifa_api.config;

import com.gifa_api.config.jwt.JwtAuthenticationFilter;
import com.gifa_api.utils.enums.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .authorizeHttpRequests(authRequest -> {
                    configurePublicEndpoints(authRequest);
                    configureAdminEndpoints(authRequest);
                    configureSupervisorEndpoints(authRequest);
                    configureGerenteEndpoints(authRequest);
                    configureOperadorEndpoints(authRequest);
                    configureAuthenticatedEndpoints(authRequest);
                })
                .sessionManagement( sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void configurePublicEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authRequest) {
        authRequest
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/ping").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
    }

    private void configureAdminEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authRequest) {
        String administrador = Rol.ADMINISTRADOR.toString();
        authRequest
                .requestMatchers(HttpMethod.POST, "/auth/register").hasRole(administrador)
                .requestMatchers(HttpMethod.POST, "/vehiculo/registrar").hasRole(administrador) // 1.1 Registrar colectivo
                .requestMatchers(HttpMethod.POST, "/inventario/registrarItem").hasRole(administrador) // 1.2 Registrar una parte para el inventario
                .requestMatchers(HttpMethod.POST, "/vehiculo/asignar").hasRole(administrador) // 1.3 Asignar una parte al vehículo
                .requestMatchers(HttpMethod.PATCH, "/vehiculo/habilitar/{id}", "/vehiculo/inhabilitar/{id}").hasRole(administrador) // 1.4 Habilitar/inhabilitar colectivo
                .requestMatchers(HttpMethod.GET, "/vehiculo/verAll").hasRole(administrador); // 1.5 Visibilizar los mantenimientos de un colectivo
    }

    private void configureSupervisorEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authRequest) {
        String supervisor = Rol.SUPERVISOR.toString();
    }

    private void configureGerenteEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authRequest) {
        String gerente = Rol.GERENTE.toString();
    }

    private void configureOperadorEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authRequest) {
        String operador = Rol.OPERADOR.toString();
        authRequest
                .requestMatchers(HttpMethod.GET, "/mantenimiento/pendientes").hasRole(operador) // 1.6 Visibilizar mantenimientos pendientes
                .requestMatchers(HttpMethod.PATCH, "/mantenimiento/asignar/{id}").hasRole(operador) // 1.7 Asignar mantenimiento
                .requestMatchers(HttpMethod.PATCH, "/mantenimiento/finalizar/{id}").hasRole(operador) // 1.8 Finalizar mantenimiento
                .requestMatchers(HttpMethod.PATCH, "/inventario/utilizarItem/{id}").hasRole(operador); // 1.9 Utilizar repuesto
    }

    private void configureAuthenticatedEndpoints(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authRequest) {
        authRequest
                .anyRequest().authenticated();
    }
}