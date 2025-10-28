package com.flowpay.banking.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utilitários para manipulação de datas
 */
public class DateUtils {

    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Retorna a data/hora atual no fuso horário do Brasil
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(BRAZIL_ZONE);
    }

    /**
     * Retorna apenas a data atual no fuso horário do Brasil
     */
    public static LocalDate today() {
        return LocalDate.now(BRAZIL_ZONE);
    }

    /**
     * Converte LocalDateTime para timestamp em milissegundos
     */
    public static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(BRAZIL_ZONE).toInstant().toEpochMilli();
    }

    /**
     * Converte timestamp em milissegundos para LocalDateTime
     */
    public static LocalDateTime fromEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), BRAZIL_ZONE);
    }

    /**
     * Formata uma data para string (dd/MM/yyyy)
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return null;
        return date.format(DATE_FORMATTER);
    }

    /**
     * Formata uma data/hora para string (dd/MM/yyyy HH:mm:ss)
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * Calcula a idade a partir da data de nascimento
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, today()).getYears();
    }

    /**
     * Verifica se é maior de idade (18 anos)
     */
    public static boolean isAdult(LocalDate birthDate) {
        return calculateAge(birthDate) >= 18;
    }

    /**
     * Retorna o início do dia (00:00:00)
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * Retorna o fim do dia (23:59:59)
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }

    /**
     * Retorna o início do mês
     */
    public static LocalDate startOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    /**
     * Retorna o fim do mês
     */
    public static LocalDate endOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    /**
     * Calcula dias entre duas datas
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Calcula meses entre duas datas
     */
    public static long monthsBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.MONTHS.between(start, end);
    }

    /**
     * Calcula anos entre duas datas
     */
    public static long yearsBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.YEARS.between(start, end);
    }

    /**
     * Verifica se a data está no passado
     */
    public static boolean isPast(LocalDate date) {
        return date.isBefore(today());
    }

    /**
     * Verifica se a data está no futuro
     */
    public static boolean isFuture(LocalDate date) {
        return date.isAfter(today());
    }

    /**
     * Adiciona dias úteis a uma data (ignora fins de semana)
     */
    public static LocalDate addBusinessDays(LocalDate date, int days) {
        LocalDate result = date;
        int addedDays = 0;

        while (addedDays < days) {
            result = result.plusDays(1);
            if (result.getDayOfWeek() != DayOfWeek.SATURDAY &&
                result.getDayOfWeek() != DayOfWeek.SUNDAY) {
                addedDays++;
            }
        }

        return result;
    }

    /**
     * Verifica se é dia útil (não é sábado nem domingo)
     */
    public static boolean isBusinessDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }

    /**
     * Retorna o dia do aniversário da conta poupança (dia do mês)
     */
    public static int getSavingsAnniversaryDay(LocalDate applicationDate) {
        return applicationDate.getDayOfMonth();
    }

    /**
     * Calcula a próxima data de aniversário da poupança
     */
    public static LocalDate getNextSavingsAnniversary(LocalDate applicationDate) {
        LocalDate today = today();
        int anniversaryDay = applicationDate.getDayOfMonth();

        LocalDate nextAnniversary = today.withDayOfMonth(
            Math.min(anniversaryDay, today.lengthOfMonth())
        );

        if (nextAnniversary.isBefore(today) || nextAnniversary.isEqual(today)) {
            nextAnniversary = nextAnniversary.plusMonths(1);
            nextAnniversary = nextAnniversary.withDayOfMonth(
                Math.min(anniversaryDay, nextAnniversary.lengthOfMonth())
            );
        }

        return nextAnniversary;
    }
}
