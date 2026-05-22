package com.tinyhouse.dto.response;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String accessToken; private String refreshToken; private String tokenType; private Long expiresIn; private UserResponse user;
    public static AuthResponse of(String at, String rt, Long exp, UserResponse user) { return AuthResponse.builder().accessToken(at).refreshToken(rt).tokenType("Bearer").expiresIn(exp).user(user).build(); }
}
