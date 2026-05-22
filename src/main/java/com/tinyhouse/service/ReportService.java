package com.tinyhouse.service;

import com.tinyhouse.dto.response.DashboardStatsResponse;
import com.tinyhouse.enums.ReservationStatus;
import com.tinyhouse.enums.Role;
import com.tinyhouse.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final TinyHouseRepository tinyHouseRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    public DashboardStatsResponse getDashboardStats() {
        return DashboardStatsResponse.builder()
                .totalUsers(userRepository.countByDeletedFalse())
                .totalOwners(userRepository.countByRole(Role.OWNER))
                .totalTenants(userRepository.countByRole(Role.TENANT))
                .totalTinyHouses(tinyHouseRepository.countByDeletedFalse())
                .activeTinyHouses(tinyHouseRepository.countByActiveTrueAndDeletedFalse())
                .totalReservations(reservationRepository.countByDeletedFalse())
                .activeReservations(reservationRepository.countByReservationStatusAndDeletedFalse(ReservationStatus.CONFIRMED))
                .pendingReservations(reservationRepository.countByReservationStatusAndDeletedFalse(ReservationStatus.PENDING))
                .totalRevenue(reservationRepository.calculateTotalRevenue())
                .monthlyRevenue(reservationRepository.calculateRevenueFrom(
                        LocalDateTime.now().minusDays(30)))
                .totalReviews(reviewRepository.countByDeletedFalse())
                .build();
    }

    public BigDecimal getOwnerRevenue(Long ownerId) {
        return reservationRepository.calculateOwnerRevenue(ownerId);
    }

    public List<Object[]> getDailyStats(int days) {
        return reservationRepository.getDailyStats(LocalDateTime.now().minusDays(days));
    }

    public List<Object[]> getMostReservedHouses(int limit) {
        return reservationRepository.getMostReservedHouses(org.springframework.data.domain.Pageable.ofSize(limit));
    }
}
