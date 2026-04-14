package com.borderless.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CorsConfig — Cross-Origin Resource Sharing configuration.
 *
 * WHAT IS CORS?
 *   Browsers enforce a "Same-Origin Policy" — a web page can only make
 *   API calls to the SAME domain it was loaded from.
 *
 *   In our case:
 *     Frontend runs at: http://localhost:3000
 *     Backend runs at:  http://localhost:8080
 *
 *   These are DIFFERENT origins (different ports = different origins).
 *   Without CORS configuration, the browser would BLOCK all API calls
 *   from the frontend to the backend with an error like:
 *     "Access to fetch at 'http://localhost:8080' from origin
 *      'http://localhost:3000' has been blocked by CORS policy"
 *
 * THIS CONFIGURATION:
 *   Tells the browser: "It's OK for any origin to call /api/** endpoints."
 *
 * NOTE: In production, replace allowedOriginPattern("*") with your actual domain.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from any origin (frontend could be on any port/domain)
        // In production: config.addAllowedOrigin("https://yourdomain.com");
        config.addAllowedOriginPattern("*");

        // Allow all standard HTTP headers (including our custom X-User-Id)
        config.addAllowedHeader("*");

        // Allow all HTTP methods: GET, POST, PUT, DELETE, OPTIONS
        config.addAllowedMethod("*");

        // Allow cookies/credentials (needed if using sessions)
        config.setAllowCredentials(true);

        // Apply this CORS config to all /api/** paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
