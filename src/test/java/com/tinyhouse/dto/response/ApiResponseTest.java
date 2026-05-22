package com.tinyhouse.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    @DisplayName("Success response should have correct fields")
    void successResponse() {
        ApiResponse<String> response = ApiResponse.success("Test data");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("Test data");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Success response with message")
    void successWithMessage() {
        ApiResponse<String> response = ApiResponse.success("Custom message", "data");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Custom message");
        assertThat(response.getData()).isEqualTo("data");
    }

    @Test
    @DisplayName("Error response should have success=false")
    void errorResponse() {
        ApiResponse<String> response = ApiResponse.error("Hata mesajı");

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Hata mesajı");
        assertThat(response.getData()).isNull();
        assertThat(response.getTimestamp()).isNotNull();
    }
}
