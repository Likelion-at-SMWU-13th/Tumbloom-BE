package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndCafeId(Long userId, Long cafeId);

    Page<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    void deleteByUserIdAndCafeId(Long userId, Long cafeId);

    @Query("SELECT f.cafe.id FROM Favorite f WHERE f.user.id = :userId")
    List<Long> findCafeIdsByUserId(Long userId);

    int countByUserId(Long userId);

    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

}
