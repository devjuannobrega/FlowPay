package com.flowpay.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicação principal do FlowPay - Sistema Bancário Digital
 *
 * FlowPay é uma plataforma bancária completa que oferece:
 * - Autenticação segura com JWT
 * - Gestão de contas e transações
 * - Sistema PIX
 * - Cartões de débito e crédito
 * - Investimentos e empréstimos
 * - Sistema anti-fraude
 * - Compliance e auditoria
 *
 * @author FlowPay Team
 * @version 1.0
 */
@SpringBootApplication
public class FlowPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowPayApplication.class, args);
	}

}
