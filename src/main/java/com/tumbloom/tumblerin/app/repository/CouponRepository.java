package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.Cafe;
import com.tumbloom.tumblerin.app.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    int countByUserId(Long userId); // 누적 발급 횟수

    int countByUserIdAndIsUsedFalse(Long userId); // 사용 가능한 쿠폰 수

    @Query("SELECT DISTINCT c.couponManager.cafe FROM Coupon c WHERE c.user.id = :userId and c.isUsed = false")
    List<Cafe> findCafeListByUserId(@Param("userId") Long userId);

}
