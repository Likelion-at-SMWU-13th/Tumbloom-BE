package com.tumbloom.tumblerin.app.repository;

import com.tumbloom.tumblerin.app.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByCafeId(Long cafeId);

}
