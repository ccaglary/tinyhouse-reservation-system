package com.tinyhouse.controller.api;

import com.tinyhouse.dto.response.ApiResponse;
import com.tinyhouse.dto.response.DashboardStatsResponse;
import com.tinyhouse.dto.response.ReservationResponse;
import com.tinyhouse.dto.response.UserResponse;
import com.tinyhouse.enums.Role;
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

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin yönetim işlemleri")
public class AdminApiController {

    private final UserService userService;
    private final ReservationService reservationService;
    private final TinyHouseService tinyHouseService;
    private final ReportService reportService;
    private final ReviewService reviewService;

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard istatistikleri")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getDashboardStats()));
    }

    @GetMapping("/users")
    @Operation(summary = "Kullanıcıları listele")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role,
            @PageableDefault(size = 20) Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(userService.searchUsers(search, pageable)));
        }
        if (role != null) {
            return ResponseEntity.ok(ApiResponse.success(userService.getUsersByRole(role, pageable)));
        }
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(pageable)));
    }

    @PutMapping("/users/{id}/toggle-status")
    @Operation(summary = "Kullanıcı durumunu değiştir")
    public ResponseEntity<ApiResponse<UserResponse>> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.toggleUserStatus(id)));
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "Kullanıcı rolünü değiştir")
    public ResponseEntity<ApiResponse<UserResponse>> changeRole(
            @PathVariable Long id, @RequestParam Role role) {
        return ResponseEntity.ok(ApiResponse.success(userService.changeUserRole(id, role)));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Kullanıcıyı sil")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Kullanıcı silindi", null));
    }

    @GetMapping("/reservations")
    @Operation(summary = "Tüm rezervasyonları listele")
    public ResponseEntity<ApiResponse<Page<ReservationResponse>>> getAllReservations(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.getAll(pageable)));
    }

    @PutMapping("/tinyhouses/{id}/toggle")
    @Operation(summary = "Tiny house aktif/pasif yap")
    public ResponseEntity<ApiResponse<?>> toggleTinyHouse(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(tinyHouseService.toggleActive(id)));
    }

    @DeleteMapping("/tinyhouses/{id}")
    @Operation(summary = "Tiny house sil")
    public ResponseEntity<ApiResponse<Void>> deleteTinyHouse(@PathVariable Long id) {
        tinyHouseService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success("Tiny house silindi", null));
    }

    @DeleteMapping("/reviews/{id}")
    @Operation(summary = "Yorumu sil")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Yorum silindi", null));
    }

    @GetMapping("/reports/daily")
    @Operation(summary = "Günlük istatistikler")
    public ResponseEntity<ApiResponse<List<Object[]>>> getDailyStats(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getDailyStats(days)));
    }

    @GetMapping("/reports/popular")
    @Operation(summary = "En popüler evler")
    public ResponseEntity<ApiResponse<List<Object[]>>> getPopularHouses(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getMostReservedHouses(limit)));
    }
}
