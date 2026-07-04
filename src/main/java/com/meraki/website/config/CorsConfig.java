//package com.meraki.website.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        // Define global CORS configuration for all endpoints
//        registry.addMapping("/**")  // Apply to all endpoints
//                .allowedOrigins("*") // List allowed origins
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Allowed HTTP methods
//                .allowedHeaders("*")  // Allowed headers
//                .allowCredentials(false)  // Allow credentials like cookies
//                .maxAge(3600);  // Cache pre-flight responses for 1 hour
//    }
//}