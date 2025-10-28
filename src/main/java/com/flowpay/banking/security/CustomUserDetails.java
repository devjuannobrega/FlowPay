package com.flowpay.banking.security;

import com.flowpay.banking.entity.User;
import com.flowpay.banking.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementação customizada de UserDetails
 */
@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private Set<UserRole> roles;
    private boolean isActive;
    private boolean isDeleted;

    public static CustomUserDetails fromUser(User user) {
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getSenha(),
                user.getRoles(),
                user.isAtivo(),
                user.getDeleted()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive && !isDeleted;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive && !isDeleted;
    }
}
