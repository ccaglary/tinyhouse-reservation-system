package com.tinyhouse.exception;

import com.tinyhouse.dto.response.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should handle ResourceNotFoundException with 404")
    void handleNotFound() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleNotFound(new ResourceNotFoundException("User", "id", 1L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("User");
    }

    @Test
    @DisplayName("Should handle BusinessException with 400")
    void handleBusiness() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleBusiness(new BusinessException("İş kuralı hatası"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).isEqualTo("İş kuralı hatası");
    }

    @Test
    @DisplayName("Should handle UnauthorizedException with 401")
    void handleUnauth() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleUnauth(new UnauthorizedException("Yetkisiz"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
    }
}
