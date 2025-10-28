package com.flowpay.banking.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilitários para manipulação de valores monetários
 */
public class CurrencyUtils {

    private static final Locale BRAZIL_LOCALE = new Locale("pt", "BR");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(BRAZIL_LOCALE);

    /**
     * Converte um valor String para BigDecimal
     */
    public static BigDecimal parse(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // Remove símbolos de moeda e espaços
            value = value.replaceAll("[R$\\s]", "").replace(",", ".");
            return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Formata um BigDecimal para formato monetário brasileiro (R$ 1.234,56)
     */
    public static String format(BigDecimal value) {
        if (value == null) {
            return "R$ 0,00";
        }
        return CURRENCY_FORMAT.format(value);
    }

    /**
     * Arredonda um valor para 2 casas decimais usando HALF_UP
     */
    public static BigDecimal round(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula a porcentagem de um valor
     */
    public static BigDecimal calculatePercentage(BigDecimal value, BigDecimal percentage) {
        if (value == null || percentage == null) {
            return BigDecimal.ZERO;
        }
        return value.multiply(percentage)
                   .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula juros simples
     *
     * @param principal Capital inicial
     * @param rate      Taxa de juros (%)
     * @param periods   Número de períodos
     * @return Valor total com juros
     */
    public static BigDecimal calculateSimpleInterest(BigDecimal principal, BigDecimal rate, int periods) {
        if (principal == null || rate == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal interest = principal
            .multiply(rate)
            .multiply(new BigDecimal(periods))
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        return principal.add(interest);
    }

    /**
     * Calcula juros compostos
     *
     * @param principal Capital inicial
     * @param rate      Taxa de juros (%)
     * @param periods   Número de períodos
     * @return Valor total com juros compostos
     */
    public static BigDecimal calculateCompoundInterest(BigDecimal principal, BigDecimal rate, int periods) {
        if (principal == null || rate == null || periods <= 0) {
            return principal != null ? principal : BigDecimal.ZERO;
        }

        // M = P * (1 + i)^n
        BigDecimal rateDecimal = rate.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        BigDecimal onePlusRate = BigDecimal.ONE.add(rateDecimal);
        BigDecimal multiplier = onePlusRate.pow(periods);

        return principal.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula o valor da parcela usando Tabela Price
     *
     * @param principal        Valor do empréstimo
     * @param monthlyRate      Taxa mensal (%)
     * @param numberOfPayments Número de parcelas
     * @return Valor da parcela
     */
    public static BigDecimal calculatePriceInstallment(BigDecimal principal, BigDecimal monthlyRate, int numberOfPayments) {
        if (principal == null || monthlyRate == null || numberOfPayments <= 0) {
            return BigDecimal.ZERO;
        }

        // i = taxa / 100
        BigDecimal rate = monthlyRate.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);

        // Se taxa for zero, divide igualmente
        if (rate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(new BigDecimal(numberOfPayments), 2, RoundingMode.HALF_UP);
        }

        // PMT = P * [i * (1+i)^n] / [(1+i)^n - 1]
        BigDecimal onePlusRate = BigDecimal.ONE.add(rate);
        BigDecimal power = onePlusRate.pow(numberOfPayments);
        BigDecimal numerator = rate.multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        BigDecimal installment = principal
            .multiply(numerator)
            .divide(denominator, 2, RoundingMode.HALF_UP);

        return installment;
    }

    /**
     * Calcula multa por atraso (padrão 2%)
     */
    public static BigDecimal calculateLateFee(BigDecimal amount) {
        return calculatePercentage(amount, new BigDecimal("2.00"));
    }

    /**
     * Calcula juros de mora diário (padrão 0.033% ao dia = 1% ao mês)
     */
    public static BigDecimal calculateDailyInterest(BigDecimal amount, int daysLate) {
        BigDecimal dailyRate = new BigDecimal("0.033"); // 1% ao mês / 30 dias
        return calculatePercentage(amount, dailyRate.multiply(new BigDecimal(daysLate)));
    }

    /**
     * Verifica se o valor é positivo
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica se o valor é zero
     */
    public static boolean isZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Verifica se o valor é negativo
     */
    public static boolean isNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Retorna o maior valor entre dois
     */
    public static BigDecimal max(BigDecimal a, BigDecimal b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.max(b);
    }

    /**
     * Retorna o menor valor entre dois
     */
    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.min(b);
    }

    /**
     * Soma segura de BigDecimals (trata nulos como zero)
     */
    public static BigDecimal safeAdd(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.add(b);
    }

    /**
     * Subtração segura de BigDecimals (trata nulos como zero)
     */
    public static BigDecimal safeSubtract(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.subtract(b);
    }
}
