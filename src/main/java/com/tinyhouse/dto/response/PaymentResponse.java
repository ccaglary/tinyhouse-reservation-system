package com.tinyhouse.dto.response;
import com.tinyhouse.enums.*; import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentResponse { private Long id; private Long reservationId; private PaymentMethod paymentMethod; private PaymentStatus paymentStatus; private BigDecimal amount; private String transactionId; private LocalDateTime paymentDate; }
