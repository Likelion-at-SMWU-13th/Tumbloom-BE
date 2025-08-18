package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CafeRepository extends JpaRepository<Cafe, Long> {

    Optional<Cafe> findByCafeName(String cafeName);
    Optional<Cafe> findByQrLink(String qrLink);
    Optional<Cafe> findByVerificationCode(String verificationCode);

    // 카페 검색
    @Query("""
        SELECT c FROM Cafe c
        WHERE REPLACE(LOWER(c.cafeName), ' ', '') LIKE CONCAT('%', LOWER(REPLACE(:keyword, ' ', '')), '%')
            OR REPLACE(LOWER(c.address), ' ', '') LIKE CONCAT('%', LOWER(REPLACE(:keyword, ' ', '')), '%')
""")
    List<Cafe> searchByCafeNameOrAddress(@Param("keyword") String keyword);

    // 3km 이내 카페 리스트
    @Query(value = """
    SELECT c.*
    FROM cafe c
    WHERE ST_Distance_Sphere(
            c.location,
            ST_SRID(POINT(:lon, :lat), 4326)
          ) <= :radiusMeters
    ORDER BY ST_Distance_Sphere(
            c.location,
            ST_SRID(POINT(:lon, :lat), 4326)
          )
    """, nativeQuery = true)
    List<Cafe> findNearbyCafeList(@Param("lon") double lon, @Param("lat") double lat, @Param("radiusMeters") double radiusMeters);

    // top5
    @Query(value = """
    SELECT c.*
    FROM cafe c
    WHERE ST_Distance_Sphere(
            c.location,
            ST_SRID(POINT(:lon, :lat), 4326)
          ) <= :radiusMeters
    ORDER BY ST_Distance_Sphere(
            c.location,
            ST_SRID(POINT(:lon, :lat), 4326)
          )
    LIMIT 5
    """, nativeQuery = true)
    List<Cafe> findNearbyTop5CafeList(@Param("lon") double lon, @Param("lat") double lat, @Param("radiusMeters") double radiusMeters);



    // 카페목록 조회용 _ 직원확인코드를 위한
    List<Cafe> findAllByVerificationCodeIsNotNullOrderByCafeNameAsc();

}
