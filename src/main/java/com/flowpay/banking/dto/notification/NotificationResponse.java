package com.flowpay.banking.dto.notification;

import com.flowpay.banking.enums.NotificationStatus;
import com.flowpay.banking.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta de notificação
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
