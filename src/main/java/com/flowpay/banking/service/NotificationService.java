package com.flowpay.banking.service;

import com.flowpay.banking.dto.notification.NotificationResponse;
import com.flowpay.banking.dto.notification.SendNotificationRequest;
import com.flowpay.banking.entity.Notification;
import com.flowpay.banking.entity.User;
import com.flowpay.banking.enums.NotificationStatus;
import com.flowpay.banking.exception.UnauthorizedException;
import com.flowpay.banking.exception.UserNotFoundException;
import com.flowpay.banking.repository.NotificationRepository;
import com.flowpay.banking.repository.UserRepository;
import com.flowpay.banking.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendNotification(SendNotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Notification notification = Notification.builder()
                .user(user)
                .notificationType(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // TODO: Implementar envio real (email, SMS, push)
        log.info("Notificação {} enviada para usuário {}", request.getType(), request.getUserId());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {
        Long userId = SecurityUtils.getCurrentUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        if (!SecurityUtils.getCurrentUserId().equals(notification.getUser().getId())) {
            throw new UnauthorizedException("Acesso negado");
        }

        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .type(notification.getNotificationType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
