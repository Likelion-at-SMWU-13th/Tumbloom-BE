package com.tumbloom.tumblerin.app.controller;

import com.tumbloom.tumblerin.app.domain.Favorite;
import com.tumbloom.tumblerin.app.repository.FavoriteRepository;
import com.tumbloom.tumblerin.app.security.CustomUserDetails;
import com.tumbloom.tumblerin.app.service.FavoriteService;
import com.tumbloom.tumblerin.global.dto.ApiResponseTemplate;
import com.tumbloom.tumblerin.global.dto.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteService favoriteService;

    @PostMapping("/{cafeId}")
    public ResponseEntity<ApiResponseTemplate<String>> addFavorite(
            @PathVariable Long cafeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        favoriteService.addFavorite(userDetails.getUser().getId(), cafeId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_CREATED, "즐겨찾기가 성공적으로 추가되었습니다.");
    }

    @DeleteMapping("/{cafeId}")
    public ResponseEntity<ApiResponseTemplate<String>> deleteFavorite(
            @PathVariable Long cafeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        favoriteService.removeFavorite(userDetails.getUser().getId(), cafeId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_DELETED, "즐겨찾기가 성공적으로 취소되었습니다.");
    }
}

