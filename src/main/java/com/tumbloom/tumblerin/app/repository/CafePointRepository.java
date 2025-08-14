package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.Cafe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CafePointRepository {

    Slice<Cafe> findNearbyCafes(double lon, double lat, double radiusMeters, Pageable pageable);

    List<Cafe> findTopNearbyCafes(double lon, double lat, double radiusMeters, int size);

}
