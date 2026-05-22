package com.tinyhouse.entity;
import com.tinyhouse.enums.PaymentStatus;
import com.tinyhouse.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "reservations", indexes = {@Index(name = "idx_res_tenant", columnList = "tenant_id"),
        @Index(name = "idx_res_house", columnList = "tiny_house_id"), @Index(name = "idx_res_status", columnList = "reservation_status")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tenant_id", nullable = false) private User tenant;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tiny_house_id", nullable = false) private TinyHouse tinyHouse;
    @Column(name = "start_date", nullable = false) private LocalDate startDate;
    @Column(name = "end_date", nullable = false) private LocalDate endDate;
    @Column(name = "total_days", nullable = false) private Integer totalDays;
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2) private BigDecimal totalPrice;
    @Enumerated(EnumType.STRING) @Column(name = "reservation_status", nullable = false, length = 20) @Builder.Default private ReservationStatus reservationStatus = ReservationStatus.PENDING;
    @Enumerated(EnumType.STRING) @Column(name = "payment_status", nullable = false, length = 20) @Builder.Default private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    @Column(name = "special_request", columnDefinition = "TEXT") private String specialRequest;
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true) private Payment payment;
}
