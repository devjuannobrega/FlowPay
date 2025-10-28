package com.flowpay.banking.util;

import java.security.SecureRandom;

/**
 * Utilitário para geração de números de conta bancária
 */
public class AccountNumberGenerator {

    private static final SecureRandom random = new SecureRandom();

    /**
     * Gera um número de conta aleatório com 8 dígitos
     *
     * @return Número da conta
     */
    public static String generateAccountNumber() {
        // Gera número de 8 dígitos
        int number = 10000000 + random.nextInt(90000000);
        return String.valueOf(number);
    }

    /**
     * Calcula o dígito verificador usando módulo 11
     *
     * @param accountNumber Número da conta
     * @param agencia       Agência
     * @return Dígito verificador
     */
    public static String calculateDigit(String accountNumber, String agencia) {
        String combined = agencia + accountNumber;
        int sum = 0;
        int weight = 2;

        // Calcula soma ponderada da direita para esquerda
        for (int i = combined.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(combined.charAt(i));
            sum += digit * weight;
            weight++;
            if (weight > 9) {
                weight = 2;
            }
        }

        int remainder = sum % 11;
        int checkDigit = 11 - remainder;

        // Se o dígito for 10 ou 11, usar 0
        if (checkDigit >= 10) {
            return "0";
        }

        return String.valueOf(checkDigit);
    }

    /**
     * Gera conta completa (número + dígito verificador)
     *
     * @param agencia Agência (padrão: 0001)
     * @return Array com [accountNumber, digitoVerificador]
     */
    public static String[] generateCompleteAccount(String agencia) {
        String accountNumber = generateAccountNumber();
        String digit = calculateDigit(accountNumber, agencia);
        return new String[]{accountNumber, digit};
    }

    /**
     * Valida se o dígito verificador está correto
     *
     * @param accountNumber Número da conta
     * @param agencia       Agência
     * @param digit         Dígito verificador informado
     * @return true se o dígito estiver correto
     */
    public static boolean validateDigit(String accountNumber, String agencia, String digit) {
        String calculatedDigit = calculateDigit(accountNumber, agencia);
        return calculatedDigit.equals(digit);
    }
}
