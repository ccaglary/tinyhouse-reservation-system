package com.tinyhouse.service;

import com.tinyhouse.entity.Notification;
import com.tinyhouse.entity.User;
import com.tinyhouse.enums.NotificationType;
import com.tinyhouse.exception.ResourceNotFoundException;
import com.tinyhouse.mapper.NotificationMapper;
import com.tinyhouse.dto.response.NotificationResponse;
import com.tinyhouse.repository.NotificationRepository;
import com.tinyhouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    @Transactional
    public void createNotification(Long userId, String title, String message, NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .notificationType(type)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.debug("Notification created for user {}: {}", userId, title);
    }

    @Transactional
    public void createSystemNotification(Long userId, String title, String message) {
        createNotification(userId, title, message, NotificationType.SYSTEM_ANNOUNCEMENT);
    }

    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalseAndDeletedFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
        log.info("All notifications marked as read for user: {}", userId);
    }
}
