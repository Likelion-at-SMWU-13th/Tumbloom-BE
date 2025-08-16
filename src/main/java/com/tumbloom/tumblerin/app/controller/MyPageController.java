package com.tumbloom.tumblerin.app.controller;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeRecommendDTO;
import com.tumbloom.tumblerin.app.dto.Userdto.UserFavoriteCafeDTO;
import com.tumbloom.tumblerin.app.dto.Userdto.UserHomeInfoDTO;
import com.tumbloom.tumblerin.app.dto.Userdto.UserPreferenceDTO;
import com.tumbloom.tumblerin.app.dto.Userdto.UserMyPageResponseDTO;
import com.tumbloom.tumblerin.app.security.CustomUserDetails;
import com.tumbloom.tumblerin.app.service.CafeRecommendationService;
import com.tumbloom.tumblerin.app.service.MyPageService;
import com.tumbloom.tumblerin.app.service.UserPreferenceService;
import com.tumbloom.tumblerin.global.dto.ApiResponseTemplate;
import com.tumbloom.tumblerin.global.dto.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class MyPageController {

    private final UserPreferenceService userPreferenceService;
    private final CafeRecommendationService cafeRecommendationService;
    private final MyPageService myPageService;

    @GetMapping("/mypage")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
        UserMyPageResponseDTO userinfo = myPageService.getUserInfo(userDetails.getUser().getId());
        UserPreferenceDTO preference = userPreferenceService.getPreference(userDetails.getUser().getId());

        //user 취향 항목 조회에서 3개만 꺼내와서 조합
        List<String> combined = new ArrayList<>();
        combined.addAll(preference.getPreferredMenus());
        combined.addAll(preference.getVisitPurposes());
        combined.addAll(preference.getExtraOptions());

        List<String> topPreferences = combined.stream()
                .limit(3)
                .collect(Collectors.toList());

        userinfo.setTopPreferences(topPreferences);

        // levelProgress 계산
        int min = MyPageService.getMinStampsForLevel(userinfo.getLevel());
        int max = MyPageService.getMaxStampsForLevel(userinfo.getLevel());
        double progress = (double)(userinfo.getTumblerUsageCount() - min) / (max - min);
        progress = Math.min(progress, 1.0);// 최대 1.0으로 제한
        // 소수점 2자리로 반올림
        progress = Math.round(progress * 100.0) / 100.0;
        userinfo.setLevelProgress(progress);

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, userinfo);
    }


    @PutMapping("/preferences")
    public ResponseEntity<?> savePreference(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserPreferenceDTO dto) {

        userPreferenceService.saveOrUpdate(userDetails.getUser().getId(),dto);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED,null);
    }

    @GetMapping("/preferences")
    public ResponseEntity<?> getPreference(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserPreferenceDTO preference = userPreferenceService.getPreference(userDetails.getUser().getId());
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, preference);
    }

    @GetMapping("/cafe-recommendations")
    public ResponseEntity<?> getCafeRecommendations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CafeRecommendDTO> recommendations = cafeRecommendationService.recommendCafesForUser(userDetails.getUser().getId());
        if (recommendations == null) {
            return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, null);
        }
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, recommendations);
    }

    @GetMapping("/home")
    public ResponseEntity<?> getStamps(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserHomeInfoDTO homeInfo = myPageService.getUserHomeInfo(userDetails.getUser().getId());
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, homeInfo);
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavoriteCafes(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<UserFavoriteCafeDTO> favoriteCafes =
                myPageService.getFavoriteCafes(userDetails.getUser().getId());

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, favoriteCafes);
    }
}
