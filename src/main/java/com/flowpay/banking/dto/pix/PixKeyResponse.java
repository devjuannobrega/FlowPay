package com.flowpay.banking.dto.pix;

import com.flowpay.banking.enums.PixKeyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta de chave PIX
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixKeyResponse {

    private Long id;
    private PixKeyType keyType;
    private String keyValue;
    private Long accountId;
    private String accountNumber;
    private boolean isActive;
    private LocalDateTime createdAt;
}
