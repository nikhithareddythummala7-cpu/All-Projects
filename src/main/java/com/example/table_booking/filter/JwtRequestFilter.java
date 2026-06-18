package com.example.table_booking.filter;

import com.example.table_booking.service.MyUserDetailsService;
import com.example.table_booking.util.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestURI = request.getRequestURI();

        // Skip JWT processing for permitAll paths and public API /api/bookings/table/**
        if (requestURI.equals("/login") || requestURI.equals("/register") || requestURI.equals("/authenticate") || requestURI.startsWith("/api/bookings/table")) {
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        String jwtError = null;

        try {
            // Try to extract JWT from Authorization header
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwt);
            } else {
                // Check for JWT in cookie if no Authorization header
                String jwtCookie = null;
                if (request.getCookies() != null) {
                    for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                        if ("jwt".equals(cookie.getName())) {
                            jwtCookie = cookie.getValue();
                            break;
                        }
                    }
                }
                if (jwtCookie != null) {
                    jwt = jwtCookie;
                    username = jwtUtil.extractUsername(jwt);
                }
            }

            // If we found a JWT and username, validate and set authentication
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

        } catch (ExpiredJwtException e) {
            // Token is expired - log but don't set authentication
            // This allows Spring Security to properly deny the request with 401
            System.err.println("JWT token is expired for request: " + requestURI);
            jwtError = "JWT_EXPIRED";
        } catch (JwtException e) {
            // Invalid token format or signature
            System.err.println("Invalid JWT token for request: " + requestURI + " - " + e.getMessage());
            jwtError = "JWT_INVALID";
        } catch (Exception e) {
            // Any other error
            System.err.println("JWT processing error for request: " + requestURI + " - " + e.getMessage());
            jwtError = "JWT_ERROR";
        }
        
        chain.doFilter(request, response);
    }
}