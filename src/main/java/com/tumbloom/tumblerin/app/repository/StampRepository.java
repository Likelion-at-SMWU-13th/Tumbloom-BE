package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StampRepository extends JpaRepository<Stamp, Long> {
    int countByUserId(Long userId);
}
