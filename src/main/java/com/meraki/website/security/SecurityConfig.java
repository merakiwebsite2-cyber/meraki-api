package com.meraki.website.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
// public class SecurityConfig {

//@Bean
//public CorsConfigurationSource corsConfigurationSource()
//{
//    CorsConfiguration config = new CorsConfiguration();
//    config.setAllowedOriginPatterns(List.of("*"));
//    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//    config.setAllowedHeaders(List.of("*"));
//    config.setAllowCredentials(true);
//    config.setMaxAge(3600L);
//
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", config);
//    return source;
//}

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
//             throws Exception {
//         http
//                 .csrf(csrf -> csrf.disable())
//                 .cors(Customizer.withDefaults())
//                 .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers(HttpMethod.POST, "/auth/signup", "/auth/login").permitAll()
//                         .requestMatchers(HttpMethod.GET, "/login/heartbeat").permitAll()
//                         .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
//                         .requestMatchers("/admin/**").hasRole("ADMIN")
//                         .requestMatchers("/user/**").hasRole("ADMIN")
//                         .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
//                         .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
//                         .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
//                         .requestMatchers("/api/images/**").hasRole("ADMIN")
//                         .anyRequest().authenticated()
//                 )
//                 .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }
// }


public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("https://meraki-interiors.ae/","http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
                throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .cors(Customizer.withDefaults())
                    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/auth/signup", "/auth/login").permitAll()
                            .requestMatchers(HttpMethod.GET, "/login/heartbeat").permitAll()
                            .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                            .requestMatchers("/admin/**").permitAll()
                            .requestMatchers("/user/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/products/**").permitAll()
                            .requestMatchers(HttpMethod.PUT, "/products/**").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/products/**").permitAll()
                            .requestMatchers("/api/images/**").permitAll()
                            .requestMatchers("/sample-requests/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }
    }

