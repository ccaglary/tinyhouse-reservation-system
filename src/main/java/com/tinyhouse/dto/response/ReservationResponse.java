package com.tinyhouse.dto.response;
import com.tinyhouse.enums.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReservationResponse { private Long id; private Long tenantId; private String tenantName; private Long tinyHouseId; private String tinyHouseTitle; private String tinyHouseCoverImage; private String tinyHouseCity; private LocalDate startDate; private LocalDate endDate; private Integer totalDays; private BigDecimal totalPrice; private ReservationStatus reservationStatus; private PaymentStatus paymentStatus; private String specialRequest; private LocalDateTime createdAt; }
