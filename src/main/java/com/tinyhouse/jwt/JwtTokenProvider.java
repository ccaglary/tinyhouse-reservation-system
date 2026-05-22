package com.tinyhouse.jwt;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Component;
import javax.crypto.SecretKey; import java.util.Base64; import java.util.Date;
@Component @Slf4j
public class JwtTokenProvider {
    private final SecretKey key;
    @Value("${app.jwt.access-token-expiration:900000}") private long accessExpiration;
    @Value("${app.jwt.refresh-token-expiration:604800000}") private long refreshExpiration;
    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret) { this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)); }
    public String generateAccessToken(String email) { return buildToken(email, accessExpiration); }
    public String generateRefreshToken(String email) { return buildToken(email, refreshExpiration); }
    public long getAccessTokenExpiration() { return accessExpiration; }
    private String buildToken(String email, long exp) { return Jwts.builder().subject(email).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + exp)).signWith(key).compact(); }
    public String getEmailFromToken(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject(); }
    public boolean validateToken(String token) { try { Jwts.parser().verifyWith(key).build().parseSignedClaims(token); return true; } catch (Exception e) { log.debug("Invalid JWT: {}", e.getMessage()); return false; } }
}
