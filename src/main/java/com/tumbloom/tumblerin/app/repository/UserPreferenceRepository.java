package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.domain.UserPreference;
import com.tumbloom.tumblerin.app.dto.UserPreferenceDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    //null도 감쌀 수 있어야 되니가 Optional
    Optional<UserPreference> findByUser(User user);

    //LazyInitializationException 우회 추후 성능 저하 발생시 ->  JPQL 또는 QueryDSL 활용
    @EntityGraph(attributePaths = {
    "visitPurposes",
    "preferredMenus",
    "extraOptions"
    })
    Optional<UserPreference> findDetailByUserId(@Param("userId") Long userId);
}
