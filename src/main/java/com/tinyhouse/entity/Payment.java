package com.tinyhouse.entity;
import com.tinyhouse.enums.PaymentMethod;
import com.tinyhouse.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "payments") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reservation_id", nullable = false, unique = true) private Reservation reservation;
    @Enumerated(EnumType.STRING) @Column(name = "payment_method", nullable = false, length = 20) private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING) @Column(name = "payment_status", nullable = false, length = 20) @Builder.Default private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    @Column(name = "amount", nullable = false, precision = 12, scale = 2) private BigDecimal amount;
    @Column(name = "transaction_id", unique = true, length = 100) private String transactionId;
    @Column(name = "payment_date") private LocalDateTime paymentDate;
}
