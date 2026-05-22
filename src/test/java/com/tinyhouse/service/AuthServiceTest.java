package com.tinyhouse.service;

import com.tinyhouse.dto.request.RegisterRequest;
import com.tinyhouse.dto.response.AuthResponse;
import com.tinyhouse.entity.User;
import com.tinyhouse.enums.Role;
import com.tinyhouse.exception.BusinessException;
import com.tinyhouse.jwt.JwtTokenProvider;
import com.tinyhouse.mapper.UserMapper;
import com.tinyhouse.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider tokenProvider;
    @Mock private UserMapper userMapper;
    @Mock private EmailService emailService;
    @Mock private NotificationService notificationService;

    @InjectMocks private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .password("password123")
                .role("TENANT")
                .build();
    }

    @Test
    @DisplayName("Register - should throw when email already exists")
    void register_shouldThrowWhenEmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("zaten kayıtlı");
    }

    @Test
    @DisplayName("Register - should create user successfully")
    void register_shouldCreateUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(tokenProvider.generateAccessToken(anyString())).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(anyString())).thenReturn("refresh-token");
        when(tokenProvider.getAccessTokenExpiration()).thenReturn(900000L);

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        verify(userRepository, times(2)).save(any(User.class));
        verify(emailService).sendVerificationEmail(anyString(), anyString(), anyString());
    }
}
