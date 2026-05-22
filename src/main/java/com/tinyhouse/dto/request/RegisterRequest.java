package com.tinyhouse.dto.request;
import jakarta.validation.constraints.*; import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequest {
    @NotBlank @Size(min=2,max=50) private String firstName;
    @NotBlank @Size(min=2,max=50) private String lastName;
    @NotBlank @Email private String email;
    @NotBlank @Size(min=6,max=100) private String password;
    private String phoneNumber;
    private String role;
}
