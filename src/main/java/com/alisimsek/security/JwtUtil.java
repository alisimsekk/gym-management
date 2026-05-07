package com.alisimsek.security;

import com.alisimsek.dto.response.AuthResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "jwt")
@Slf4j
@Getter
@Setter
public class JwtUtil {

    private final ConcurrentHashMap<String, Date> activeTokens = new ConcurrentHashMap<>();

    private String secret;
    private long expiration;
    private String issuer;

    public AuthResponse getAuthResponse(UserDetails userDetails) {
        String accessToken = generateToken(userDetails);
        Date expirationDate = getExpiration(accessToken);

        activeTokens.put(accessToken, expirationDate);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .expirationDate(expirationDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }

    private String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setId(UUID.randomUUID().toString())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public Date getExpiration(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            if (!activeTokens.containsKey(token)) {
                log.warn("Token not found in active tokens: {}", token);
                return false;
            }

            Date expirationDate = activeTokens.get(token);
            boolean isTokenExpired = expirationDate.before(new Date());

            String username = getUsernameFromToken(token);

            return (username.equals(userDetails.getUsername()) && !isTokenExpired);
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void removeTokenFromActiveTokensList(String token) {
        activeTokens.remove(token);
        log.info("Token removed from active tokens list.");
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
