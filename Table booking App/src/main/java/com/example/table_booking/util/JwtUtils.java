package com.example.table_booking.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // Default 24 hours
    private long expirationMs;
    
    private Key getSigningKey() {
        try {
            // First try to decode as Base64
            if (secret.length() >= 32) { // Reasonable minimum length for a secure key
                try {
                    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
                } catch (Exception e) {
                    // If not Base64, use the string as is
                    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                }
            }
            // If key is too short, use the secure default
            String secureKey = "KMyXy4z8bQ7rP9wE6uT3nA5vH2sR8jK1mF4cZ7dQ9uW2xL5bV8yT6nQ3kP1rD8";
            return Keys.hmacShaKeyFor(secureKey.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Error creating signing key", e);
            throw new IllegalStateException("Failed to create signing key", e);
        }
    }
    
    public String extractUsername(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            return extractUsername(token);
        }
        return null;
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            logger.warn("JWT Token expired: {}", ex.getMessage());
            throw ex;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException ex) {
            logger.warn("Invalid JWT token: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error processing JWT token: {}", ex.getMessage(), ex);
            throw new JwtException("Error processing JWT token", ex);
        }
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateToken(UserDetails userDetails) {
        return createToken(userDetails.getUsername());
    }
    
    private String createToken(String subject) {
        try {
            return Jwts.builder()
                    .setSubject(subject)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS384)
                    .compact();
        } catch (Exception e) {
            logger.error("Error creating JWT token", e);
            throw new JwtException("Failed to create token", e);
        }
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
