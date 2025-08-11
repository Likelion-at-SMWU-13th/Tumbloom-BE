package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.domain.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    //null도 감쌀 수 있어야 되니가 Optional
    Optional<UserPreference> findByUser(User user);
}
