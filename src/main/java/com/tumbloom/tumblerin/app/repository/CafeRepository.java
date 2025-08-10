package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CafeRepository extends JpaRepository<Cafe, Long> {

    Optional<Cafe> findByCafeName(String cafeName);
    Optional<Cafe> findByQrLink(String qrLink);
    Optional<Cafe> findByVerificationCode(String verificationCode);

}
