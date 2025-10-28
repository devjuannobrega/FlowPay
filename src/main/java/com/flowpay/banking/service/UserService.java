package com.flowpay.banking.service;

import com.flowpay.banking.dto.user.ChangePasswordRequest;
import com.flowpay.banking.dto.user.UpdateUserRequest;
import com.flowpay.banking.dto.user.UserResponse;
import com.flowpay.banking.entity.User;
import com.flowpay.banking.enums.UserRole;
import com.flowpay.banking.exception.BusinessException;
import com.flowpay.banking.exception.UnauthorizedException;
import com.flowpay.banking.exception.UserNotFoundException;
import com.flowpay.banking.repository.UserRepository;
import com.flowpay.banking.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service para gestão de usuários
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retorna informações do usuário autenticado
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        return getUserById(userId);
    }

    /**
     * Retorna informações de um usuário específico
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        // Verificar permissão
        if (!SecurityUtils.canAccessResource(userId)) {
            throw new UnauthorizedException("Acesso negado");
        }

        return mapToUserResponse(user);
    }

    /**
     * Atualiza informações do usuário
     */
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        // Verificar permissão
        if (!SecurityUtils.canAccessResource(userId)) {
            throw new UnauthorizedException("Acesso negado");
        }

        // Atualizar campos se fornecidos
        if (request.getNomeCompleto() != null) {
            user.setNomeCompleto(request.getNomeCompleto());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // Verificar se email já existe
            if (userRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
                throw new BusinessException("Email já está em uso");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getTelefone() != null) {
            user.setTelefone(request.getTelefone());
        }

        if (request.getEndereco() != null) {
            user.setEndereco(request.getEndereco());
        }

        if (request.getCidade() != null) {
            user.setCidade(request.getCidade());
        }

        if (request.getEstado() != null) {
            user.setEstado(request.getEstado());
        }

        if (request.getCep() != null) {
            user.setCep(request.getCep());
        }

        if (request.getProfissao() != null) {
            user.setProfissao(request.getProfissao());
        }

        if (request.getEmpresaTrabalho() != null) {
            user.setEmpresaTrabalho(request.getEmpresaTrabalho());
        }

        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        log.info("Usuário atualizado: ID {}", userId);

        return mapToUserResponse(user);
    }

    /**
     * Altera a senha do usuário
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        // Verificar permissão
        if (!SecurityUtils.getCurrentUserId().equals(userId)) {
            throw new UnauthorizedException("Acesso negado");
        }

        // Verificar senha atual
        if (!passwordEncoder.matches(request.getSenhaAtual(), user.getSenha())) {
            throw new BusinessException("Senha atual incorreta");
        }

        // Verificar se nova senha é diferente da atual
        if (passwordEncoder.matches(request.getNovaSenha(), user.getSenha())) {
            throw new BusinessException("Nova senha deve ser diferente da senha atual");
        }

        // Verificar confirmação de senha
        if (!request.getNovaSenha().equals(request.getConfirmacaoNovaSenha())) {
            throw new BusinessException("Senhas não coincidem");
        }

        // Atualizar senha
        user.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Senha alterada para usuário: ID {}", userId);
    }

    /**
     * Ativa 2FA para o usuário
     */
    @Transactional
    public void enable2FA(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        if (!SecurityUtils.getCurrentUserId().equals(userId)) {
            throw new UnauthorizedException("Acesso negado");
        }

        user.setTwoFactorEnabled(true);
        // TODO: Gerar e retornar secret key para TOTP
        userRepository.save(user);

        log.info("2FA ativado para usuário: ID {}", userId);
    }

    /**
     * Desativa 2FA para o usuário
     */
    @Transactional
    public void disable2FA(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        if (!SecurityUtils.getCurrentUserId().equals(userId)) {
            throw new UnauthorizedException("Acesso negado");
        }

        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);

        log.info("2FA desativado para usuário: ID {}", userId);
    }

    /**
     * Deleta (soft delete) o usuário
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        if (!SecurityUtils.getCurrentUserId().equals(userId) && !SecurityUtils.isAdmin()) {
            throw new UnauthorizedException("Acesso negado");
        }

        user.setDeleted(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Usuário deletado (soft delete): ID {}", userId);
    }

    /**
     * Mapeia User para UserResponse
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nomeCompleto(user.getNomeCompleto())
                .cpf(user.getCpf())
                .email(user.getEmail())
                .telefone(user.getTelefone())
                .dataNascimento(user.getDataNascimento())
                .idade(LocalDate.now().getYear() - user.getDataNascimento().getYear())
                .endereco(user.getEndereco())
                .cidade(user.getCidade())
                .estado(user.getEstado())
                .cep(user.getCep())
                .nomeMae(user.getNomeMae())
                .profissao(user.getProfissao())
                .empresaTrabalho(user.getEmpresaTrabalho())
                .status(user.getStatus())
                .roles(user.getRoles())
                .twoFactorEnabled(user.getTwoFactorEnabled())
                .lastLogin(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
