package com.flowpay.banking.service;

import com.flowpay.banking.dto.auth.*;
import com.flowpay.banking.entity.Account;
import com.flowpay.banking.entity.Referral;
import com.flowpay.banking.entity.User;
import com.flowpay.banking.enums.AccountStatus;
import com.flowpay.banking.enums.ReferralStatus;
import com.flowpay.banking.enums.UserRole;
import com.flowpay.banking.enums.UserStatus;
import com.flowpay.banking.exception.BusinessException;
import com.flowpay.banking.exception.InvalidCpfException;
import com.flowpay.banking.exception.UnauthorizedException;
import com.flowpay.banking.repository.AccountRepository;
import com.flowpay.banking.repository.ReferralRepository;
import com.flowpay.banking.repository.UserRepository;
import com.flowpay.banking.security.JwtTokenProvider;
import com.flowpay.banking.util.AccountNumberGenerator;
import com.flowpay.banking.util.CpfValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Service para autenticação e registro de usuários
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ReferralRepository referralRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    /**
     * Registra um novo usuário e cria sua conta
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        log.info("Registrando novo usuário: {}", request.getEmail());

        // Validar CPF
        if (!CpfValidator.isValid(request.getCpf())) {
            throw new InvalidCpfException("CPF inválido");
        }

        // Verificar se usuário já existe
        if (userRepository.existsByCpfAndDeletedFalse(request.getCpf())) {
            throw new BusinessException("CPF já cadastrado");
        }

        if (userRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        // Validar idade mínima (18 anos)
        int idade = LocalDate.now().getYear() - request.getDataNascimento().getYear();
        if (idade < 18) {
            throw new BusinessException("Idade mínima para cadastro é 18 anos");
        }

        // Criar usuário
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_USER);

        User user = User.builder()
                .nomeCompleto(request.getNomeCompleto())
                .cpf(request.getCpf())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .telefone(request.getTelefone())
                .dataNascimento(request.getDataNascimento())
                .endereco(request.getEndereco())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .cep(request.getCep())
                .status(UserStatus.ATIVO)
                .roles(roles)
                .twoFactorEnabled(false)
                .loginAttempts(0)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        user = userRepository.save(user);
        log.info("Usuário criado com sucesso: ID {}", user.getId());

        // Criar conta
        String[] accountDetails = AccountNumberGenerator.generateCompleteAccount("0001");

        Account account = Account.builder()
                .user(user)
                .agencia("0001")
                .accountNumber(accountDetails[0])
                .digitoVerificador(accountDetails[1])
                .type(request.getTipoConta())
                .saldo(BigDecimal.ZERO)
                .saldoBloqueado(BigDecimal.ZERO)
                .limiteDiario(new BigDecimal("5000.00"))
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .build();

        accountRepository.save(account);
        log.info("Conta criada com sucesso: {}-{}", account.getAccountNumber(), account.getDigitoVerificador());

        // Gerar tokens
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getSenha()
        );
        authentication = authenticationManager.authenticate(authentication);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .nomeCompleto(user.getNomeCompleto())
                .roles(user.getRoles())
                .twoFactorRequired(false)
                .build();
    }

    /**
     * Realiza login do usuário
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Tentativa de login: {}", request.getEmail());

        User user = userRepository.findByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        // Verificar se usuário está bloqueado
        if (user.getStatus() == UserStatus.BLOQUEADO) {
            throw new UnauthorizedException("Usuário bloqueado. Entre em contato com o suporte.");
        }

        if (user.getStatus() == UserStatus.SUSPENSO) {
            throw new UnauthorizedException("Usuário suspenso. Entre em contato com o suporte.");
        }

        // Verificar limite de tentativas
        if (user.getLoginAttempts() >= 5) {
            user.setStatus(UserStatus.BLOQUEADO);
            userRepository.save(user);
            throw new UnauthorizedException("Conta bloqueada por excesso de tentativas de login");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );

            // Reset tentativas de login
            user.setLoginAttempts(0);
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // Verificar 2FA
            if (user.getTwoFactorEnabled()) {
                // TODO: Implementar lógica de verificação 2FA
                if (request.getTwoFactorCode() == null || request.getTwoFactorCode().isEmpty()) {
                    return LoginResponse.builder()
                            .twoFactorRequired(true)
                            .userId(user.getId())
                            .email(user.getEmail())
                            .build();
                }
            }

            String accessToken = tokenProvider.generateAccessToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);

            log.info("Login realizado com sucesso: {}", user.getEmail());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .nomeCompleto(user.getNomeCompleto())
                    .roles(user.getRoles())
                    .twoFactorRequired(false)
                    .build();

        } catch (BadCredentialsException e) {
            // Incrementar tentativas de login
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            userRepository.save(user);
            log.warn("Tentativa de login falhada para: {}", request.getEmail());
            throw new UnauthorizedException("Credenciais inválidas");
        }
    }

    /**
     * Atualiza o access token usando refresh token
     */
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Refresh token inválido ou expirado");
        }

        if (!tokenProvider.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Token fornecido não é um refresh token");
        }

        String userEmail = tokenProvider.getUserEmailFromToken(refreshToken);
        User user = userRepository.findByEmailAndDeletedFalse(userEmail)
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

        // Criar nova autenticação
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                user.getRoles().stream()
                        .map(role -> (org.springframework.security.core.GrantedAuthority) () -> role.name())
                        .toList()
        );

        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);

        log.info("Tokens atualizados para usuário: {}", userEmail);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .build();
    }

    /**
     * Realiza logout (invalidação de token seria feita com Redis/cache)
     */
    public void logout(String token) {
        // TODO: Implementar blacklist de tokens com Redis
        log.info("Logout realizado");
    }
}
