package com.TeachCode.Gestion_Courses.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class CourseServiceSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    public CourseServiceSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    // In CourseServiceSecurityConfig( Course Microservice)
    @Bean
    public SecurityFilterChain  securityFilterChain(HttpSecurity http) throws Exception {
     http.csrf(AbstractHttpConfigurer::disable)
             .csrf(cors -> cors.disable())
             .authorizeHttpRequests(auth->auth
                     .anyRequest().permitAll());
     return http.build();
    }

    }

