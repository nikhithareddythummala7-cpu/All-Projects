package com.example.table_booking.config;

import com.example.table_booking.filter.JwtRequestFilter;
import com.example.table_booking.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080", "http://127.0.0.1:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

    // PUBLIC HTML PAGES
    .requestMatchers(
            "/login",
            "/register",
            "/authenticate",
            "/admin/**",
            "/dashboard/**",
            "/css/**",
            "/js/**",
            "/images/**"
    ).permitAll()

    // PUBLIC RESTAURANT APIS
    .requestMatchers(
            "/api/restaurants/list",
            "/api/restaurants",
            "/api/restaurants/*"
    ).permitAll()

    // ⭐ PUBLIC BOOKING TABLES API (NO AUTH REQUIRED)
    .requestMatchers("/api/bookings/table/**").permitAll()

    // ⭐ AUTHENTICATED BOOKING APIS (JWT REQUIRED)
    .requestMatchers(
            "/api/bookings/create",
            "/api/bookings/*/confirm",
            "/api/bookings/*/cancel",
            "/api/bookings/user/**"
    ).authenticated()

    // API ADMIN ENDPOINTS (JWT REQUIRED)
    .requestMatchers("/api/admin/**").authenticated()

    // ALL OTHER /api NEED JWT
    .requestMatchers("/api/**").authenticated()

    // EVERYTHING ELSE PUBLIC
    .anyRequest().permitAll()
)

            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form.disable());

        return http.build();
    }
}
