package com.tinyhouse.dto.response;
import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewResponse { private Long id; private BigDecimal rating; private String comment; private String ownerReply; private Long tinyHouseId; private String tinyHouseTitle; private Long tenantId; private String tenantName; private String tenantProfileImage; private LocalDateTime createdAt; }
