package com.tinyhouse.scheduler;

import com.tinyhouse.entity.Reservation;
import com.tinyhouse.enums.ReservationStatus;
import com.tinyhouse.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void completeExpiredReservations() {
        log.info("Running scheduled task: completeExpiredReservations");
        var confirmed = reservationRepository.findByReservationStatusAndDeletedFalse(
                ReservationStatus.CONFIRMED, Pageable.unpaged());

        int count = 0;
        for (Reservation r : confirmed) {
            if (r.getEndDate().isBefore(LocalDate.now())) {
                r.setReservationStatus(ReservationStatus.COMPLETED);
                reservationRepository.save(r);
                count++;
            }
        }
        log.info("Completed {} expired reservations", count);
    }

    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void cancelStalePendingReservations() {
        log.info("Running scheduled task: cancelStalePendingReservations");
        var pending = reservationRepository.findByReservationStatusAndDeletedFalse(
                ReservationStatus.PENDING, Pageable.unpaged());

        int count = 0;
        for (Reservation r : pending) {
            if (r.getCreatedAt() != null && r.getCreatedAt().plusHours(48).isBefore(java.time.LocalDateTime.now())) {
                r.setReservationStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(r);
                count++;
            }
        }
        log.info("Auto-cancelled {} stale pending reservations", count);
    }
}
