package com.example.retirementCalculator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * WebConfig class configures CORS settings and other web-related configurations
 * for the Retirement Calculator API.
 * <p>
 * This configuration allows cross-origin requests from the Angular frontend
 * (typically running on http://localhost:4200) and other specified origins.
 * It enables HTTP methods commonly used in RESTful APIs and allows credentials
 * to be sent with requests.
 * </p>
 *
 * <p>
 * Additional web configuration customizations can be added to this class
 * by implementing methods from the {@link WebMvcConfigurer} interface.
 * </p>
 *
 * @author Your Name
 * @since 1.0
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
} 