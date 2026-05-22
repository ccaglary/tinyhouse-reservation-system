package com.tinyhouse.service;

import com.tinyhouse.dto.request.LoginRequest;
import com.tinyhouse.dto.request.PasswordResetRequest;
import com.tinyhouse.dto.request.RegisterRequest;
import com.tinyhouse.dto.response.AuthResponse;
import com.tinyhouse.dto.response.UserResponse;
import com.tinyhouse.entity.User;
import com.tinyhouse.enums.NotificationType;
import com.tinyhouse.enums.Role;
import com.tinyhouse.exception.BusinessException;
import com.tinyhouse.exception.ResourceNotFoundException;
import com.tinyhouse.jwt.JwtTokenProvider;
import com.tinyhouse.mapper.UserMapper;
import com.tinyhouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Bu e-posta adresi zaten kayıtlı");
        }
        Role role = Role.TENANT;
        try { if (request.getRole() != null) role = Role.valueOf(request.getRole()); } catch (Exception ignored) {}

        String verificationToken = UUID.randomUUID().toString();
        User user = User.builder()
                .firstName(request.getFirstName()).lastName(request.getLastName())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber()).role(role).active(true).emailVerified(false)
                .verificationToken(verificationToken).build();
        user = userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationToken);
        notificationService.createNotification(user.getId(), "Hoş Geldiniz!", "TinyHouse'a hoş geldiniz. Hesabınız başarıyla oluşturuldu.", NotificationType.SYSTEM_ANNOUNCEMENT);
        log.info("User registered: {}", user.getEmail());
        return AuthResponse.of(accessToken, refreshToken, tokenProvider.getAccessTokenExpiration(), userMapper.toResponse(user));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmailAndDeletedFalse(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
        String accessToken = tokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        log.info("User logged in: {}", user.getEmail());
        return AuthResponse.of(accessToken, refreshToken, tokenProvider.getAccessTokenExpiration(), userMapper.toResponse(user));
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) throw new BusinessException("Geçersiz refresh token");
        String email = tokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByRefreshTokenAndDeletedFalse(refreshToken).orElseThrow(() -> new BusinessException("Refresh token bulunamadı"));
        String newAccess = tokenProvider.generateAccessToken(email);
        String newRefresh = tokenProvider.generateRefreshToken(email);
        user.setRefreshToken(newRefresh);
        userRepository.save(user);
        return AuthResponse.of(newAccess, newRefresh, tokenProvider.getAccessTokenExpiration(), userMapper.toResponse(user));
    }

    public void logout(String email) { userRepository.findByEmailAndDeletedFalse(email).ifPresent(u -> { u.setRefreshToken(null); userRepository.save(u); }); }

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationTokenAndDeletedFalse(token).orElseThrow(() -> new BusinessException("Geçersiz doğrulama tokeni"));
        user.setEmailVerified(true); user.setVerificationToken(null); userRepository.save(user);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmailAndDeletedFalse(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken); userRepository.save(user);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetToken);
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        User user = userRepository.findByResetTokenAndDeletedFalse(request.getToken()).orElseThrow(() -> new BusinessException("Geçersiz sıfırlama tokeni"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null); userRepository.save(user);
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmailAndDeletedFalse(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toResponse(user);
    }
}
