package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.Cafe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CafePointRepository {
    Page<Cafe> findNearbyCafes(double lon, double lat, Pageable pageable);
}
