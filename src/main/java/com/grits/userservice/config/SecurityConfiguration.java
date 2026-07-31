package com.grits.userservice.config;

import com.grits.userservice.converter.KeycloakRoleConverter;
import com.grits.userservice.security.CardAuthorizationManager;
import com.grits.userservice.security.UserAuthorizationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            UserAuthorizationManager userAuthorizationManager,
            CardAuthorizationManager cardAuthorizationManager
    ) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/v1/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/users/{id}").access(userAuthorizationManager)
                        .requestMatchers(HttpMethod.PATCH, "/v1/users/{id}").access(userAuthorizationManager)
                        .requestMatchers(HttpMethod.PATCH, "/v1/users/{id}/activate").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/v1/users/{id}/deactivate").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/v1/cards").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/cards/{id}").access(cardAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/v1/cards/user/{id}").access(userAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/v1/cards/user/{id}").access(userAuthorizationManager)
                        .requestMatchers(HttpMethod.PATCH, "/v1/cards/{id}").access(cardAuthorizationManager)
                        .requestMatchers(HttpMethod.PATCH, "/v1/cards/{id}/activate").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/v1/cards/{id}/deactivate").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(KeycloakRoleConverter rolesConverter) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(rolesConverter);
        return converter;
    }
}
