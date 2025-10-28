package com.flowpay.banking.dto.notification;

import com.flowpay.banking.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para envio de notificação
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {

    @NotNull(message = "ID do usuário é obrigatório")
    private Long userId;

    @NotNull(message = "Tipo de notificação é obrigatório")
    private NotificationType type;

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "Mensagem é obrigatória")
    private String message;
}
