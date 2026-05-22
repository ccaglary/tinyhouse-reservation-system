package com.tinyhouse.dto.request;
import com.tinyhouse.enums.PaymentMethod; import jakarta.validation.constraints.*; import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentRequest { @NotNull private Long reservationId; @NotNull private PaymentMethod paymentMethod; private String cardNumber; private String cardHolder; private String expiryDate; private String cvv; }
