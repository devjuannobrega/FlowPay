package com.flowpay.banking.repository;

import com.flowpay.banking.entity.Notification;
import com.flowpay.banking.enums.NotificationStatus;
import com.flowpay.banking.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para operações com notificações
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndDeletedFalse(Long userId);

    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);

    List<Notification> findByNotificationType(NotificationType notificationType);

    @Query("SELECT n FROM Notification n WHERE " +
           "n.user.id = :userId " +
           "AND n.status = 'UNREAD' " +
           "AND n.deleted = false " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE " +
           "n.user.id = :userId " +
           "AND n.status = 'UNREAD' " +
           "AND n.deleted = false")
    long countUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE " +
           "n.user.id = :userId " +
           "AND n.deleted = false " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE " +
           "n.user.id = :userId " +
           "AND n.notificationType = :type " +
           "AND n.deleted = false " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndType(@Param("userId") Long userId, @Param("type") NotificationType type);

    @Query("SELECT n FROM Notification n WHERE " +
           "n.createdAt < :date " +
           "AND n.status = 'ARCHIVED' " +
           "AND n.deleted = false")
    List<Notification> findOldArchivedNotifications(@Param("date") LocalDateTime date);

    @Query("SELECT n FROM Notification n WHERE " +
           "n.relatedTransactionId = :transactionId " +
           "AND n.deleted = false")
    List<Notification> findByTransactionId(@Param("transactionId") Long transactionId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
