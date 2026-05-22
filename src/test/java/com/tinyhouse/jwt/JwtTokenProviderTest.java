package com.tinyhouse.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() throws Exception {
        String secret = "dGlueWhvdXNlLXNlY3JldC1rZXktZm9yLWp3dC1hdXRoZW50aWNhdGlvbi1lbnRlcnByaXNlLXByb2R1Y3Rpb24tcmVhZHk=";
        tokenProvider = new JwtTokenProvider(secret);

        // Set expiration values via reflection since @Value won't be processed without Spring
        Field accessExp = JwtTokenProvider.class.getDeclaredField("accessExpiration");
        accessExp.setAccessible(true);
        accessExp.set(tokenProvider, 900000L); // 15 minutes

        Field refreshExp = JwtTokenProvider.class.getDeclaredField("refreshExpiration");
        refreshExp.setAccessible(true);
        refreshExp.set(tokenProvider, 604800000L); // 7 days
    }

    @Test
    @DisplayName("Should generate valid access token")
    void generateAccessToken() {
        String token = tokenProvider.generateAccessToken("test@test.com");

        assertThat(token).isNotBlank();
        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should extract email from token")
    void getEmailFromToken() {
        String token = tokenProvider.generateAccessToken("user@example.com");
        String email = tokenProvider.getEmailFromToken(token);

        assertThat(email).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("Should reject invalid token")
    void validateToken_invalid() {
        assertThat(tokenProvider.validateToken("invalid.token.here")).isFalse();
    }

    @Test
    @DisplayName("Should reject empty token")
    void validateToken_empty() {
        assertThat(tokenProvider.validateToken("")).isFalse();
    }

    @Test
    @DisplayName("Access and refresh tokens should both be valid for same user")
    void generateBothTokens() {
        String access = tokenProvider.generateAccessToken("test@test.com");
        String refresh = tokenProvider.generateRefreshToken("test@test.com");

        assertThat(tokenProvider.validateToken(access)).isTrue();
        assertThat(tokenProvider.validateToken(refresh)).isTrue();
        assertThat(tokenProvider.getEmailFromToken(access)).isEqualTo("test@test.com");
        assertThat(tokenProvider.getEmailFromToken(refresh)).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("Should return correct expiration value")
    void getAccessTokenExpiration() {
        assertThat(tokenProvider.getAccessTokenExpiration()).isEqualTo(900000L);
    }
}
