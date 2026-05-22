package com.tinyhouse.controller.api;

import com.tinyhouse.dto.request.LoginRequest;
import com.tinyhouse.dto.request.PasswordResetRequest;
import com.tinyhouse.dto.request.RegisterRequest;
import com.tinyhouse.dto.response.ApiResponse;
import com.tinyhouse.dto.response.AuthResponse;
import com.tinyhouse.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Kimlik doğrulama işlemleri")
public class AuthApiController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Yeni kullanıcı kaydı")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Kayıt başarılı", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Kullanıcı girişi")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Giriş başarılı", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Token yenileme")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestParam String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Çıkış yap")
    public ResponseEntity<ApiResponse<Void>> logout(Principal principal) {
        authService.logout(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Çıkış başarılı", null));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "E-posta doğrulama")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("E-posta doğrulandı", null));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Şifre sıfırlama talebi")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok(ApiResponse.success("Şifre sıfırlama e-postası gönderildi", null));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Şifre sıfırlama")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Şifre başarıyla sıfırlandı", null));
    }

    @GetMapping("/me")
    @Operation(summary = "Mevcut kullanıcı bilgisi")
    public ResponseEntity<ApiResponse<?>> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(authService.getCurrentUser(principal.getName())));
    }
}
