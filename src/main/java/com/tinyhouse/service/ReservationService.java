package com.tinyhouse.service;

import com.tinyhouse.dto.request.ReservationRequest;
import com.tinyhouse.dto.response.ReservationResponse;
import com.tinyhouse.entity.Reservation;
import com.tinyhouse.entity.TinyHouse;
import com.tinyhouse.entity.User;
import com.tinyhouse.enums.NotificationType;
import com.tinyhouse.enums.ReservationStatus;
import com.tinyhouse.exception.BusinessException;
import com.tinyhouse.exception.ResourceNotFoundException;
import com.tinyhouse.mapper.ReservationMapper;
import com.tinyhouse.repository.ReservationRepository;
import com.tinyhouse.repository.TinyHouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Service @RequiredArgsConstructor @Slf4j
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TinyHouseRepository tinyHouseRepository;
    private final ReservationMapper reservationMapper;
    private final UserService userService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Transactional
    public ReservationResponse create(ReservationRequest request, String tenantEmail) {
        User tenant = userService.findUserByEmail(tenantEmail);
        TinyHouse house = tinyHouseRepository.findByIdAndDeletedFalse(request.getTinyHouseId())
                .orElseThrow(() -> new ResourceNotFoundException("TinyHouse", "id", request.getTinyHouseId()));
        if (!house.isActive()) throw new BusinessException("Bu ev şu anda aktif değil");
        if (request.getStartDate().isAfter(request.getEndDate()) || request.getStartDate().isEqual(request.getEndDate()))
            throw new BusinessException("Geçersiz tarih aralığı");
        if (reservationRepository.existsConflict(house.getId(), request.getStartDate(), request.getEndDate()))
            throw new BusinessException("Bu tarihler için çakışan rezervasyon var");

        int totalDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        BigDecimal totalPrice = house.getNightlyPrice().multiply(BigDecimal.valueOf(totalDays)).add(house.getCleaningFee() != null ? house.getCleaningFee() : BigDecimal.ZERO);

        Reservation reservation = Reservation.builder().tenant(tenant).tinyHouse(house)
                .startDate(request.getStartDate()).endDate(request.getEndDate()).totalDays(totalDays).totalPrice(totalPrice)
                .specialRequest(request.getSpecialRequest()).build();
        reservation = reservationRepository.save(reservation);

        notificationService.createNotification(house.getOwner().getId(), "Yeni Rezervasyon",
                tenant.getFirstName() + " " + house.getTitle() + " için rezervasyon yaptı.", NotificationType.RESERVATION_CREATED);
        log.info("Reservation created: {} for house {} by {}", reservation.getId(), house.getId(), tenantEmail);
        return reservationMapper.toResponse(reservation);
    }

    @Transactional(readOnly = true)
    public ReservationResponse getById(Long id) { return reservationMapper.toResponse(reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id))); }
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getByTenant(String email, Pageable pageable) { User tenant = userService.findUserByEmail(email); return reservationRepository.findByTenantIdAndDeletedFalse(tenant.getId(), pageable).map(reservationMapper::toResponse); }
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getByOwner(String email, Pageable pageable) { User owner = userService.findUserByEmail(email); return reservationRepository.findByTinyHouseOwnerIdAndDeletedFalse(owner.getId(), pageable).map(reservationMapper::toResponse); }
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getAll(Pageable pageable) { return reservationRepository.findByDeletedFalse(pageable).map(reservationMapper::toResponse); }

    @Transactional
    public ReservationResponse confirmReservation(Long id, String ownerEmail) {
        Reservation res = reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        if (!res.getTinyHouse().getOwner().getEmail().equals(ownerEmail)) throw new BusinessException("Yetkiniz yok");
        if (res.getReservationStatus() != ReservationStatus.PENDING) throw new BusinessException("Yalnızca bekleyen rezervasyonlar onaylanabilir");
        res.setReservationStatus(ReservationStatus.CONFIRMED); reservationRepository.save(res);
        notificationService.createNotification(res.getTenant().getId(), "Rezervasyon Onaylandı", res.getTinyHouse().getTitle() + " için rezervasyonunuz onaylandı!", NotificationType.RESERVATION_CONFIRMED);
        emailService.sendReservationConfirmation(res.getTenant().getEmail(), res.getTenant().getFirstName(), res);
        return reservationMapper.toResponse(res);
    }

    @Transactional
    public ReservationResponse rejectReservation(Long id, String ownerEmail) {
        Reservation res = reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        if (!res.getTinyHouse().getOwner().getEmail().equals(ownerEmail)) throw new BusinessException("Yetkiniz yok");
        res.setReservationStatus(ReservationStatus.REJECTED); reservationRepository.save(res);
        notificationService.createNotification(res.getTenant().getId(), "Rezervasyon Reddedildi", res.getTinyHouse().getTitle() + " için rezervasyonunuz reddedildi.", NotificationType.RESERVATION_REJECTED);
        return reservationMapper.toResponse(res);
    }

    @Transactional
    public ReservationResponse cancelReservation(Long id, String tenantEmail) {
        Reservation res = reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        if (!res.getTenant().getEmail().equals(tenantEmail)) throw new BusinessException("Yetkiniz yok");
        if (res.getReservationStatus() == ReservationStatus.COMPLETED) throw new BusinessException("Tamamlanmış rezervasyonlar iptal edilemez");
        res.setReservationStatus(ReservationStatus.CANCELLED); reservationRepository.save(res);
        notificationService.createNotification(res.getTinyHouse().getOwner().getId(), "Rezervasyon İptal", res.getTenant().getFirstName() + " rezervasyonunu iptal etti.", NotificationType.RESERVATION_CANCELLED);
        return reservationMapper.toResponse(res);
    }
}
