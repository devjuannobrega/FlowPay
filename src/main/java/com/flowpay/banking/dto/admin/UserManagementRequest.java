package com.flowpay.banking.dto.admin;

import com.flowpay.banking.enums.UserRole;
import com.flowpay.banking.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO para gestão de usuário pelo admin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementRequest {

    private UserStatus status;
    private Set<UserRole> roles;
    private String adminNotes;
}
