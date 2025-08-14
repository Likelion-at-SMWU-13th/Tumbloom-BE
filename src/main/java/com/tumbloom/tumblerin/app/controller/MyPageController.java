package com.tumbloom.tumblerin.app.controller;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeRecommendDTO;
import com.tumbloom.tumblerin.app.dto.UserPreferenceDTO;
import com.tumbloom.tumblerin.app.security.CustomUserDetails;
import com.tumbloom.tumblerin.app.service.CafeRecommendationService;
import com.tumbloom.tumblerin.app.service.UserPreferenceService;
import com.tumbloom.tumblerin.global.dto.ApiResponseTemplate;
import com.tumbloom.tumblerin.global.dto.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class MyPageController {

    private final UserPreferenceService userPreferenceService;
    private final CafeRecommendationService cafeRecommendationService;

    @PostMapping("/preferences")
    public ResponseEntity<?> savePreference(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserPreferenceDTO dto) {

        userPreferenceService.saveOrUpdate(userDetails.getUser().getId(),dto);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED,null);
    }

    @GetMapping("/preferences")
    public ResponseEntity<?> getPreference(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserPreferenceDTO preferenceDTO = userPreferenceService.getPreference(userDetails.getUser().getId());
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, preferenceDTO);
    }

    @GetMapping("/cafe-recommendations")
    public ResponseEntity<?> getCafeRecommendations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CafeRecommendDTO> recommendations = cafeRecommendationService.recommendCafesForUser(userDetails.getUser().getId());
        if (recommendations == null) {
            return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, null);
        }
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, recommendations);
    }
}
