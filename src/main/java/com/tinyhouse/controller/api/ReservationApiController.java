package com.tinyhouse.controller.api;

import com.tinyhouse.dto.request.ReservationRequest;
import com.tinyhouse.dto.response.ApiResponse;
import com.tinyhouse.dto.response.ReservationResponse;
import com.tinyhouse.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Rezervasyon yönetimi")
public class ReservationApiController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Rezervasyon oluştur")
    public ResponseEntity<ApiResponse<ReservationResponse>> create(
            @Valid @RequestBody ReservationRequest request, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Rezervasyon oluşturuldu",
                reservationService.create(request, principal.getName())));
    }

    @GetMapping("/my")
    @Operation(summary = "Kendi rezervasyonlarım")
    public ResponseEntity<ApiResponse<Page<ReservationResponse>>> getMyReservations(
            Principal principal, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.getByTenant(principal.getName(), pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Rezervasyon detayı")
    public ResponseEntity<ApiResponse<ReservationResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getById(id)));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Rezervasyon iptal et")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancel(
            @PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.cancelReservation(id, principal.getName())));
    }
}
