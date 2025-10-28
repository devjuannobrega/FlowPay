package com.flowpay.banking.util;

import java.security.SecureRandom;

/**
 * Implementação do Algoritmo de Luhn para validação de números de cartão
 */
public class LuhnAlgorithm {

    private static final SecureRandom random = new SecureRandom();

    /**
     * Valida um número de cartão usando o algoritmo de Luhn
     *
     * @param cardNumber Número do cartão (16 dígitos)
     * @return true se o número for válido
     */
    public static boolean validate(String cardNumber) {
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;

        // Percorre da direita para esquerda
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = digit - 9;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }

    /**
     * Calcula o dígito verificador de Luhn para um número parcial de cartão
     *
     * @param partialCardNumber Número parcial do cartão (15 dígitos)
     * @return Dígito verificador
     */
    public static int calculateCheckDigit(String partialCardNumber) {
        if (partialCardNumber == null || !partialCardNumber.matches("\\d{15}")) {
            throw new IllegalArgumentException("Número parcial deve ter 15 dígitos");
        }

        int sum = 0;
        boolean alternate = true; // Começa true porque estamos calculando para o último dígito

        for (int i = partialCardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(partialCardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = digit - 9;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit;
    }

    /**
     * Gera um número de cartão válido usando o algoritmo de Luhn
     *
     * @param prefix Prefixo do cartão (BIN) - ex: 5555 para Mastercard
     * @return Número de cartão válido com 16 dígitos
     */
    public static String generateCardNumber(String prefix) {
        if (prefix == null || prefix.length() > 15) {
            throw new IllegalArgumentException("Prefixo inválido");
        }

        // Gera dígitos aleatórios até completar 15 dígitos
        StringBuilder cardNumber = new StringBuilder(prefix);
        int remainingDigits = 15 - prefix.length();

        for (int i = 0; i < remainingDigits; i++) {
            cardNumber.append(random.nextInt(10));
        }

        // Calcula e adiciona o dígito verificador
        int checkDigit = calculateCheckDigit(cardNumber.toString());
        cardNumber.append(checkDigit);

        return cardNumber.toString();
    }

    /**
     * Gera um número de cartão de crédito válido (Visa)
     *
     * @return Número de cartão Visa válido
     */
    public static String generateVisaCard() {
        return generateCardNumber("4");
    }

    /**
     * Gera um número de cartão de crédito válido (Mastercard)
     *
     * @return Número de cartão Mastercard válido
     */
    public static String generateMastercardCard() {
        return generateCardNumber("5555");
    }

    /**
     * Gera o dígito verificador para um número parcial de cartão (alias para calculateCheckDigit)
     *
     * @param partialCardNumber Número parcial do cartão (15 dígitos)
     * @return Dígito verificador como String
     */
    public static String generateCheckDigit(String partialCardNumber) {
        return String.valueOf(calculateCheckDigit(partialCardNumber));
    }

    /**
     * Mascara um número de cartão (mostra apenas os últimos 4 dígitos)
     *
     * @param cardNumber Número completo do cartão
     * @return Cartão mascarado (ex: **** **** **** 1234)
     */
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }
}
