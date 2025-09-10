package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {


}
