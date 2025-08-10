package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndCafeId(Long userId, Long cafeId);

    Page<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    void deleteByUserIdAndCafeId(Long userId, Long cafeId);

}
