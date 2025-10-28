package com.flowpay.banking.dto.payment;

import com.flowpay.banking.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para resposta de pagamento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long accountId;
    private PaymentType paymentType;
    private String barcode;
    private BigDecimal valor;
    private BigDecimal multa;
    private BigDecimal juros;
    private BigDecimal valorTotal;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private boolean isPaid;
    private String descricao;
    private LocalDateTime createdAt;
}
