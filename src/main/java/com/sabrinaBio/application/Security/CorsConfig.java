package com.sabrinaBio.application.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow specific origins
        config.setAllowedOrigins(Arrays.asList(
            "https://sabrina-bio.tn",
            "https://sabrina-bio.tn:8080",
            "http://localhost:3000",
            "http://192.168.100.15:3000"
        ));
        
        // Allow all headers and methods
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        // Allow credentials
        config.setAllowCredentials(true);
        
        // Set max age for CORS preflight
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}