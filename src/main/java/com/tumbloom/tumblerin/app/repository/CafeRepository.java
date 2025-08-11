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

    @Query("""
        SELECT c FROM Cafe c
        WHERE REPLACE(LOWER(c.cafeName), ' ', '') LIKE CONCAT('%', LOWER(REPLACE(:keyword, ' ', '')), '%')
            OR REPLACE(LOWER(c.address), ' ', '') LIKE CONCAT('%', LOWER(REPLACE(:keyword, ' ', '')), '%')
""")
    List<Cafe> searchByCafeNameOrAddress(@Param("keyword") String keyword);

}
