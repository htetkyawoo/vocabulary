package com.example.vocabulary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${security.allowedMethods}")
    private String[] allowedMethods;

    @Value("${security.allowedHeaders}")
    private String[] allowedHeaders;

    @Value("${security.allowedOrigins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .allowedOrigins(allowedOrigins);
    }

}
