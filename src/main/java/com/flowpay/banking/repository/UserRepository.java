package com.flowpay.banking.repository;

import com.flowpay.banking.entity.User;
import com.flowpay.banking.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com usuários
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByCpf(String cpf);

    Optional<User> findByEmailAndDeletedFalse(String email);

    Optional<User> findByCpfAndDeletedFalse(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    List<User> findByStatus(UserStatus status);

    List<User> findByDeletedFalse();

    @Query("SELECT u FROM User u WHERE u.status = :status AND u.deleted = false")
    List<User> findActiveUsersByStatus(@Param("status") UserStatus status);

    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date AND u.status = 'ATIVO' AND u.deleted = false")
    List<User> findInactiveUsers(@Param("date") LocalDateTime date);

    @Query("SELECT u FROM User u WHERE u.loginAttempts >= 3 AND u.status = 'ATIVO' AND u.deleted = false")
    List<User> findUsersWithFailedLoginAttempts();

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status AND u.deleted = false")
    long countByStatus(@Param("status") UserStatus status);

    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.nomeCompleto) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "u.cpf LIKE CONCAT('%', :search, '%')) AND " +
           "u.deleted = false")
    List<User> searchUsers(@Param("search") String search);

    Optional<User> findByIdAndDeletedFalse(Long id);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByCpfAndDeletedFalse(String cpf);
}
