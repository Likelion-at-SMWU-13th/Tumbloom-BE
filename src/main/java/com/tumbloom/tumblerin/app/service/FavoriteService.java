package com.tumbloom.tumblerin.app.service;

import com.tumbloom.tumblerin.app.domain.Cafe;
import com.tumbloom.tumblerin.app.domain.Favorite;
import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.repository.CafeRepository;
import com.tumbloom.tumblerin.app.repository.FavoriteRepository;
import com.tumbloom.tumblerin.app.repository.UserRepository;
import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final CafeRepository cafeRepository;

    // 즐겨찾기 추가
    @Transactional
    public void addFavorite(Long userId, Long cafeId) {

        if (favoriteRepository.existsByUserIdAndCafeId(userId, cafeId)) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, null));
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "카페를 찾을 수 없습니다. id=" + cafeId));

        Favorite favorite = Favorite.builder()
                .user(user)
                .cafe(cafe)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.save(favorite);

    }

    // 즐겨찾기 삭제
    @Transactional
    public void removeFavorite(Long userId, Long cafeId) {

        favoriteRepository.deleteByUserIdAndCafeId(userId, cafeId);
    }

}
