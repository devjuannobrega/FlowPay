package com.flowpay.banking.repository;

import com.flowpay.banking.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações com favoritos
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);

    List<Favorite> findByUserIdAndDeletedFalse(Long userId);

    List<Favorite> findByFavoriteType(String favoriteType);

    @Query("SELECT f FROM Favorite f WHERE " +
           "f.user.id = :userId " +
           "AND f.favoriteType = :favoriteType " +
           "AND f.deleted = false")
    List<Favorite> findByUserIdAndType(@Param("userId") Long userId, @Param("favoriteType") String favoriteType);

    @Query("SELECT COUNT(f) FROM Favorite f WHERE " +
           "f.user.id = :userId " +
           "AND f.deleted = false")
    long countByUserId(@Param("userId") Long userId);
}
