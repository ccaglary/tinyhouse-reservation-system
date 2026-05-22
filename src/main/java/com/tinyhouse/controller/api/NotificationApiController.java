package com.tinyhouse.controller.api;

import com.tinyhouse.dto.response.ApiResponse;
import com.tinyhouse.dto.response.NotificationResponse;
import com.tinyhouse.service.NotificationService;
import com.tinyhouse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Bildirim yönetimi")
public class NotificationApiController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Bildirimlerimi listele")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
            Principal principal, @PageableDefault(size = 20) Pageable pageable) {
        var user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUserNotifications(user.getId(), pageable)));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Okunmamış bildirim sayısı")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Principal principal) {
        var user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadCount(user.getId())));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Bildirimi okundu olarak işaretle")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Bildirim okundu olarak işaretlendi", null));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Tüm bildirimleri okundu olarak işaretle")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Principal principal) {
        var user = userService.getUserByEmail(principal.getName());
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Tüm bildirimler okundu olarak işaretlendi", null));
    }
}
