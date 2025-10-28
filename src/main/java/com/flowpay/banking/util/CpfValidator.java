package com.flowpay.banking.util;

/**
 * Utilitário para validação de CPF
 */
public class CpfValidator {

    /**
     * Valida um CPF brasileiro
     *
     * @param cpf CPF com ou sem formatação
     * @return true se o CPF for válido
     */
    public static boolean isValid(String cpf) {
        if (cpf == null) {
            return false;
        }

        // Remove formatação
        cpf = cpf.replaceAll("[^0-9]", "");

        // CPF deve ter 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // CPFs inválidos conhecidos (todos dígitos iguais)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Calcula primeiro dígito verificador
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit >= 10) {
                firstDigit = 0;
            }

            // Valida primeiro dígito
            if (Character.getNumericValue(cpf.charAt(9)) != firstDigit) {
                return false;
            }

            // Calcula segundo dígito verificador
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10) {
                secondDigit = 0;
            }

            // Valida segundo dígito
            return Character.getNumericValue(cpf.charAt(10)) == secondDigit;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Remove formatação do CPF (pontos e hífen)
     *
     * @param cpf CPF com formatação
     * @return CPF sem formatação (apenas números)
     */
    public static String removeFormatting(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^0-9]", "");
    }

    /**
     * Formata um CPF
     *
     * @param cpf CPF sem formatação
     * @return CPF formatado (xxx.xxx.xxx-xx)
     */
    public static String format(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
