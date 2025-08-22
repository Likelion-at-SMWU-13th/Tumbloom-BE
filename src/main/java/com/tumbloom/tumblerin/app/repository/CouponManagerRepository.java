package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.CouponManager;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
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
    List<CouponManager> searchAvailableByCafeName(@Param("cafeName") String cafeName);

    // 발급 시 동시성 제어를 위한 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           select cm from CouponManager cm
           join fetch cm.cafe c
           where c.id = :cafeId
           """)
    Optional<CouponManager> findByCafeIdForUpdate(@Param("cafeId") Long cafeId);

    // ===== 거리순 TOP7 가용 쿠폰 카페 (내 주변) =====
    interface NearbyAvailableProjection {
        Long getCafeId();
        String getCafeName();
        Integer getRemainingQuantity();
        Double getDistanceMeters();
        String getImageUrl();
    }

    @Query(value = """
        SELECT 
            c.id                AS cafeId,
            c.cafe_name         AS cafeName,
            cm.coupon_quantity  AS remainingQuantity,
            ST_Distance_Sphere(
                c.location,
                ST_SRID(POINT(:lon, :lat), 4326)
            ) AS distanceMeters,
            c.image_url         AS imageUrl
        FROM coupon_manager cm
        JOIN cafe c ON cm.cafe_id = c.id
        WHERE cm.coupon_quantity > 0
        ORDER BY distanceMeters ASC
        LIMIT 7
    """, nativeQuery = true)
    List<NearbyAvailableProjection> findNearbyAvailableTop7(@Param("lat") double lat, @Param("lon") double lon);
}
