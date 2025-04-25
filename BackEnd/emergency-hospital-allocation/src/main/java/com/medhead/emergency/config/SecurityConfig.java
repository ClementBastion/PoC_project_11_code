package com.medhead.emergency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Security configuration class for the application.
 * <p>
 * Configures endpoint access control and JWT-based authentication.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Defines the main security filter chain used by Spring Security.
     * <p>
     * - Public access to /public/**
     * - Role-based access control for other paths
     * - All other requests require authentication
     * - Uses OAuth2 Resource Server with JWT support
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()                      // Public endpoints
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/actuator/prometheus").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")              // Admin-only
                        .requestMatchers("/doctor/**").hasRole("DOCTOR")            // Doctor-only
                        .requestMatchers("/nurse/**").hasRole("NURSE")              // Nurse-only
                        .requestMatchers("/dispatcher/**").hasRole("DISPATCHER")    // Dispatcher-only
                        .requestMatchers("/hospital_manager/**").hasRole("HOSPITAL_MANAGER") // Manager-only
                        .requestMatchers("/patient/**").hasRole("PATIENT")          // Patient-only
                        .anyRequest().authenticated()                              // Everything else requires authentication
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    /**
     * Configures the JWT decoder with the JWKS endpoint of the Keycloak server.
     * <p>
     * This is required to validate and parse JWT tokens.
     */
//    @Bean
//    public JwtDecoder jwtDecoder() {
//        return NimbusJwtDecoder.withJwkSetUri(
//                "http://localhost:8081/realms/medhead/protocol/openid-connect/certs"
//        ).build();
//    }

    /**
     * Extracts roles from the JWT token and converts them into GrantedAuthority.
     * <p>
     * Only roles starting with "ROLE_" are considered valid authorities.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>();

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                for (String role : roles) {
                    if (role.startsWith("ROLE_")) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                }
            }

            return authorities;
        });

        return converter;
    }
}
