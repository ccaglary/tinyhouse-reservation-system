package com.tinyhouse.service;

import com.tinyhouse.dto.request.ReservationRequest;
import com.tinyhouse.entity.TinyHouse;
import com.tinyhouse.entity.User;
import com.tinyhouse.enums.Role;
import com.tinyhouse.exception.BusinessException;
import com.tinyhouse.exception.ResourceNotFoundException;
import com.tinyhouse.mapper.ReservationMapper;
import com.tinyhouse.repository.ReservationRepository;
import com.tinyhouse.repository.TinyHouseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private TinyHouseRepository tinyHouseRepository;
    @Mock private ReservationMapper reservationMapper;
    @Mock private UserService userService;
    @Mock private NotificationService notificationService;
    @Mock private EmailService emailService;

    @InjectMocks private ReservationService reservationService;

    private User createTenant() {
        User u = User.builder().email("test@test.com").firstName("Test").lastName("User").password("p").role(Role.TENANT).build();
        u.setId(1L);
        return u;
    }

    private User createOwner() {
        User u = User.builder().email("owner@test.com").firstName("Owner").lastName("User").password("p").role(Role.OWNER).build();
        u.setId(2L);
        return u;
    }

    private TinyHouse createHouse(boolean active) {
        TinyHouse h = TinyHouse.builder().title("Test House").description("Desc").city("Istanbul").address("Addr")
                .active(active).nightlyPrice(BigDecimal.valueOf(100)).cleaningFee(BigDecimal.ZERO).capacity(4).owner(createOwner()).build();
        h.setId(1L);
        return h;
    }

    @Test
    @DisplayName("Create - should throw when house not found")
    void create_shouldThrowWhenHouseNotFound() {
        ReservationRequest request = ReservationRequest.builder()
                .tinyHouseId(999L).startDate(LocalDate.now().plusDays(1)).endDate(LocalDate.now().plusDays(3)).build();

        when(userService.findUserByEmail(anyString())).thenReturn(createTenant());
        when(tinyHouseRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.create(request, "test@test.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Create - should throw when date conflict exists")
    void create_shouldThrowWhenDateConflict() {
        ReservationRequest request = ReservationRequest.builder()
                .tinyHouseId(1L).startDate(LocalDate.now().plusDays(1)).endDate(LocalDate.now().plusDays(3)).build();

        when(userService.findUserByEmail(anyString())).thenReturn(createTenant());
        when(tinyHouseRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(createHouse(true)));
        when(reservationRepository.existsConflict(eq(1L), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> reservationService.create(request, "test@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("çakışan");
    }

    @Test
    @DisplayName("Create - should throw when dates are invalid")
    void create_shouldThrowWhenInvalidDates() {
        ReservationRequest request = ReservationRequest.builder()
                .tinyHouseId(1L).startDate(LocalDate.now().plusDays(5)).endDate(LocalDate.now().plusDays(3)).build();

        when(userService.findUserByEmail(anyString())).thenReturn(createTenant());
        when(tinyHouseRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(createHouse(true)));

        assertThatThrownBy(() -> reservationService.create(request, "test@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Geçersiz tarih");
    }

    @Test
    @DisplayName("Create - should throw when house is inactive")
    void create_shouldThrowWhenHouseInactive() {
        ReservationRequest request = ReservationRequest.builder()
                .tinyHouseId(1L).startDate(LocalDate.now().plusDays(1)).endDate(LocalDate.now().plusDays(3)).build();

        when(userService.findUserByEmail(anyString())).thenReturn(createTenant());
        when(tinyHouseRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(createHouse(false)));

        assertThatThrownBy(() -> reservationService.create(request, "test@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("aktif değil");
    }
}
