package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
}
