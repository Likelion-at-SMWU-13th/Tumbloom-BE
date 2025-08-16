package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.Cafe;
import com.tumbloom.tumblerin.app.domain.Stamp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StampRepository extends JpaRepository<Stamp, Long> {
    int countByUserId(Long userId);

    @Query("SELECT s.cafe FROM Stamp s GROUP BY s.cafe ORDER BY COUNT(s) DESC")
    List<Cafe> findCafeListByUserId(Pageable pageable);

}
