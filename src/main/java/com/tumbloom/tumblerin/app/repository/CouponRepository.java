package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    int countByUserId(Long userId); // 누적 발급 횟수

    int countByUserIdAndIsUsedFalse(Long userId); // 사용 가능한 쿠폰 수
}
