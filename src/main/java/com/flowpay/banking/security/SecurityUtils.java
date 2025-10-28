package com.flowpay.banking.security;

import com.flowpay.banking.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilitários para segurança
 */
public class SecurityUtils {

    /**
     * Retorna o usuário autenticado atualmente
     */
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }

        throw new UnauthorizedException("Usuário não autenticado");
    }

    /**
     * Retorna o ID do usuário autenticado
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Retorna o email do usuário autenticado
     */
    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    /**
     * Verifica se há um usuário autenticado
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
               authentication.isAuthenticated() &&
               authentication.getPrincipal() instanceof CustomUserDetails;
    }

    /**
     * Verifica se o usuário tem uma role específica
     */
    public static boolean hasRole(String role) {
        if (!isAuthenticated()) {
            return false;
        }

        return getCurrentUser().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    /**
     * Verifica se o usuário é admin
     */
    public static boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    /**
     * Verifica se o usuário pode acessar um recurso
     * (é o próprio usuário ou é admin)
     */
    public static boolean canAccessResource(Long userId) {
        if (!isAuthenticated()) {
            return false;
        }

        return getCurrentUserId().equals(userId) || isAdmin();
    }
}
