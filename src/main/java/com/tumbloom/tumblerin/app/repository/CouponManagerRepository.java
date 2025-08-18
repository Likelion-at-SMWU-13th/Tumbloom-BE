package com.tumbloom.tumblerin.app.repository;


import com.tumbloom.tumblerin.app.domain.CouponManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface CouponManagerRepository extends JpaRepository<CouponManager, Long> {

    // 교환 가능(남은 수량 > 0) 카페 전부
    @Query("""
           select cm from CouponManager cm
           join fetch cm.cafe c
           where cm.couponQuantity > 0
           order by c.cafeName asc
           """)
    List<CouponManager> findAllAvailable();

    // 교환 가능 + 카페명 검색
    @Query("""
           select cm from CouponManager cm
           join fetch cm.cafe c
           where cm.couponQuantity > 0
             and lower(c.cafeName) like lower(concat('%', :cafeName, '%'))
           order by c.cafeName asc
           """)
    List<CouponManager> searchAvailableByCafeName(String cafeName);

    // 발급 시 동시성 제어를 위한 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           select cm from CouponManager cm
           join fetch cm.cafe c
           where c.id = :cafeId
           """)
    Optional<CouponManager> findByCafeIdForUpdate(Long cafeId);
}
