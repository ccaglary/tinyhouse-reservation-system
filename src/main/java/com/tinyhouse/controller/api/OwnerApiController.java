package com.tinyhouse.controller.api;

import com.tinyhouse.dto.response.ApiResponse;
import com.tinyhouse.dto.response.ReservationResponse;
import com.tinyhouse.dto.response.TinyHouseResponse;
import com.tinyhouse.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
@Tag(name = "Owner", description = "Ev sahibi işlemleri")
public class OwnerApiController {

    private final TinyHouseService tinyHouseService;
    private final ReservationService reservationService;
    private final ReportService reportService;
    private final UserService userService;

    @GetMapping("/my-houses")
    @Operation(summary = "Evlerimi listele")
    public ResponseEntity<ApiResponse<Page<TinyHouseResponse>>> getMyHouses(
            Principal principal, @PageableDefault(size = 10) Pageable pageable) {
        var user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(ApiResponse.success(tinyHouseService.getByOwner(user.getId(), pageable)));
    }

    @GetMapping("/reservations")
    @Operation(summary = "Evlerime gelen rezervasyonlar")
    public ResponseEntity<ApiResponse<Page<ReservationResponse>>> getOwnerReservations(
            Principal principal, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.getByOwner(principal.getName(), pageable)));
    }

    @PutMapping("/reservations/{id}/confirm")
    @Operation(summary = "Rezervasyonu onayla")
    public ResponseEntity<ApiResponse<ReservationResponse>> confirm(
            @PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.confirmReservation(id, principal.getName())));
    }

    @PutMapping("/reservations/{id}/reject")
    @Operation(summary = "Rezervasyonu reddet")
    public ResponseEntity<ApiResponse<ReservationResponse>> reject(
            @PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.rejectReservation(id, principal.getName())));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Gelir bilgisi")
    public ResponseEntity<ApiResponse<BigDecimal>> getRevenue(Principal principal) {
        var user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(ApiResponse.success(reportService.getOwnerRevenue(user.getId())));
    }
}
