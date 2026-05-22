package com.tinyhouse.dto.response;
import com.tinyhouse.enums.Role; import lombok.*; import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse { private Long id; private String firstName; private String lastName; private String email; private String phoneNumber; private Role role; private boolean active; private String profileImage; private boolean emailVerified; private LocalDateTime createdAt; }
