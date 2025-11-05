package mw.maulidi.money_manager_springboot_starter_api.config;

import lombok.RequiredArgsConstructor;
import mw.maulidi.money_manager_springboot_starter_api.service.AppUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration class for the API.
 * -------------------------------------------------------
 * This class defines how the application handles authentication,
 * authorization, password encryption, and cross-origin requests (CORS).
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    public final AppUserDetailsService appUserDetailsService;

    /**
     * Configures the main Spring Security filter chain.
     *
     * - Enables CORS (allows cross-origin API access)
     * - Disables CSRF (since we’re building a stateless REST API)
     * - Permits unauthenticated access to public endpoints
     * - Requires authentication for all other requests
     * - Disables HTTP sessions (stateless = every request must be authenticated)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // Enable CORS with default configuration
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection for REST APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/status",
                                "/",
                                "/health",
                                "/api/v1/profiles/register",
                                "/api/v1/profiles/activate",
                                "/api/v1/profiles/login"
                        ).permitAll() // Publicly accessible endpoints
                        .anyRequest().authenticated() // All other endpoints require authentication
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // here No sessions — JWT or token-based
                );

        return http.build();
    }

    /**
     * Defines the password encoder to use across the application.
     *
     * BCrypt is a strong one-way hashing algorithm ideal for storing passwords securely.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) to allow frontend apps
     * (e.g., React, Angular, etc.) to interact with this backend securely.
     *
     * - Allows requests from any origin (*)
     * - Supports standard HTTP methods
     * - Allows necessary headers like Authorization and Content-Type
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("*")); // Allow all origins
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Common HTTP methods
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "Accept")); // Allowed headers
        configuration.setAllowCredentials(true); // Allow sending cookies/auth tokens

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all endpoints

        return source;
    }

    @Bean
    public AuthenticationManager  authenticationManagerBean() throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(appUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(Arrays.asList(daoAuthenticationProvider));
    }
}
