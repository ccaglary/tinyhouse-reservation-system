package com.tinyhouse.dto.request;
import jakarta.validation.constraints.*; import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PasswordResetRequest { @NotBlank private String token; @NotBlank private String newPassword; }
