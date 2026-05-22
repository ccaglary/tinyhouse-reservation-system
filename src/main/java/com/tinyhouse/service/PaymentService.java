package com.tinyhouse.service;

import com.tinyhouse.dto.request.PaymentRequest;
import com.tinyhouse.dto.response.PaymentResponse;
import com.tinyhouse.entity.Payment;
import com.tinyhouse.entity.Reservation;
import com.tinyhouse.enums.NotificationType;
import com.tinyhouse.enums.PaymentStatus;
import com.tinyhouse.enums.ReservationStatus;
import com.tinyhouse.exception.BusinessException;
import com.tinyhouse.exception.ResourceNotFoundException;
import com.tinyhouse.mapper.PaymentMapper;
import com.tinyhouse.repository.PaymentRepository;
import com.tinyhouse.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ReservationRepository reservationRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, String tenantEmail) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", request.getReservationId()));

        if (!reservation.getTenant().getEmail().equals(tenantEmail)) {
            throw new BusinessException("Bu rezervasyon için ödeme yapma yetkiniz yok");
        }

        if (reservation.getReservationStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessException("Yalnızca onaylanmış rezervasyonlar için ödeme yapılabilir");
        }

        if (reservation.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new BusinessException("Bu rezervasyon için ödeme zaten yapılmış");
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        boolean paymentSuccess = simulatePayment(request);

        Payment payment = Payment.builder()
                .reservation(reservation)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(paymentSuccess ? PaymentStatus.COMPLETED : PaymentStatus.FAILED)
                .amount(reservation.getTotalPrice())
                .transactionId(transactionId)
                .paymentDate(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        if (paymentSuccess) {
            reservation.setPaymentStatus(PaymentStatus.COMPLETED);
            reservationRepository.save(reservation);

            notificationService.createNotification(reservation.getTenant().getId(),
                    "Ödeme Başarılı", "Ödemeniz başarıyla tamamlandı. İşlem No: " + transactionId,
                    NotificationType.PAYMENT_COMPLETED);

            notificationService.createNotification(reservation.getTinyHouse().getOwner().getId(),
                    "Ödeme Alındı", reservation.getTenant().getFirstName() + " ödeme yaptı.",
                    NotificationType.PAYMENT_COMPLETED);

            emailService.sendPaymentConfirmation(reservation.getTenant().getEmail(),
                    reservation.getTenant().getFirstName(), transactionId, reservation.getTotalPrice().toString());

            log.info("Payment completed: {} for reservation {}", transactionId, reservation.getId());
        } else {
            reservation.setPaymentStatus(PaymentStatus.FAILED);
            reservationRepository.save(reservation);

            notificationService.createNotification(reservation.getTenant().getId(),
                    "Ödeme Başarısız", "Ödeme işlemi başarısız oldu. Lütfen tekrar deneyin.",
                    NotificationType.PAYMENT_FAILED);

            log.warn("Payment failed for reservation {}", reservation.getId());
        }

        return paymentMapper.toResponse(payment);
    }

    public Page<PaymentResponse> getAll(Pageable pageable) {
        return paymentRepository.findByDeletedFalse(pageable).map(paymentMapper::toResponse);
    }

    public Page<PaymentResponse> getByTenant(Long tenantId, Pageable pageable) {
        return paymentRepository.findByReservationTenantIdAndDeletedFalse(tenantId, pageable)
                .map(paymentMapper::toResponse);
    }

    public PaymentResponse getByReservation(Long reservationId) {
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "reservationId", reservationId));
        return paymentMapper.toResponse(payment);
    }

    private boolean simulatePayment(PaymentRequest request) {

        if (request.getCardNumber() != null && request.getCardNumber().endsWith("0000")) {
            return false;
        }
        return true;
    }
}
