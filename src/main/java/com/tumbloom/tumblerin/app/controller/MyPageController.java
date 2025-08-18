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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "마이페이지 내 정보 조회",
            description = """
    사용자 정보, 레벨, 쿠폰 수, 즐겨찾기 수, 상위 선호도 3개를 반환합니다.
    - 이때 필드 중 remainingSteps는 다음 단계까지 남은 횟수를 가리킵니다.
    - levelProgress는 level 바에 표시하기 위한 수치값으로 0~1의 값을 반환합니다.""")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserMyPageResponseDTO.class)))
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
        double progress = (double)(userinfo.getTumblerCount() - min) / (max - min);
        progress = Math.min(progress, 1.0);// 최대 1.0으로 제한
        // 소수점 2자리로 반올림
        progress = Math.round(progress * 100.0) / 100.0;
        userinfo.setLevelProgress(progress);

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, userinfo);
    }

    @Operation(
            summary = "마이 페이지 선호도 저장/업데이트",
            description = """
        사용자의 선호도를 저장하거나 업데이트합니다. 요청 및 응답에 쓰이는 값들은 아래로 고정입니다.

        가능한 값:
        - visitPurposes (방문 목적):
          "EMOTIONAL_ATMOSPHERE", "STUDY_WORKSPACE", "CHAT_MEETING", "HOT_PLACE", "EVENT"
        - preferredMenus (선호 메뉴):
          "SPECIALTY", "DESSERT", "DECAF", "SEASON_MENU", "BRUNCH"
        - extraOptions (추가 옵션):
          "FRANCHISE", "PET_FRIENDLY", "OUTDOOR_TERRACE", "ECO_LOCAL", "UNIQUE_THEME"
        """
    )
    @ApiResponse(responseCode = "200", description = "성공")
    @PutMapping("/preferences")
    public ResponseEntity<?> savePreference(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserPreferenceDTO dto) {

        userPreferenceService.saveOrUpdate(userDetails.getUser().getId(),dto);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_UPDATED,null);
    }

    @Operation(summary = "(참고)선호도 조회", description = "사용자의 선호도를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserPreferenceDTO.class)))
    @GetMapping("/preferences")
    public ResponseEntity<?> getPreference(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserPreferenceDTO preference = userPreferenceService.getPreference(userDetails.getUser().getId());
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, preference);
    }

    @Operation(summary = "홈 화면 AI 카페 추천 목록 조회", description = "사용자 취향 기반 AI 카페 추천 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CafeRecommendDTO.class))))
    @GetMapping("/cafe-recommendations")
    public ResponseEntity<?> getCafeRecommendations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CafeRecommendDTO> recommendations = cafeRecommendationService.recommendCafesForUser(userDetails.getUser().getId());
        if (recommendations == null) {
            return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, null);
        }
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, recommendations);
    }

    @Operation(summary = "홈 화면 유저 정보 조회",
            description = """
        홈 화면에서의 히어로 섹션과 도장판 정보를 반환합니다.
        이때, 도장판 정보에서
        - stampSummary는 현재 유효한 stamp 수를 n/8 의 형태로 반환합니다.
        - currentStampCount는 도장판에 찍혀야 할 도장의 개수를 가리킵니다.
        - canExchangeCoupon는 현재 유효한 stamp 수가 8개 이상인지 알 수 있는 플래그 값입니다.
        """)
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(schema = @Schema(implementation = UserHomeInfoDTO.class)))
    @GetMapping("/home")
    public ResponseEntity<?> getStamps(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserHomeInfoDTO homeInfo = myPageService.getUserHomeInfo(userDetails.getUser().getId());
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, homeInfo);
    }

    @Operation(summary = "마이페이지 즐겨찾기 카페 목록 조회", description = "사용자가 즐겨찾기한 카페 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserFavoriteCafeDTO.class))))
    @GetMapping("/favorites")
    public ResponseEntity<?> getFavoriteCafes(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<UserFavoriteCafeDTO> favoriteCafes =
                myPageService.getFavoriteCafes(userDetails.getUser().getId());

        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, favoriteCafes);
    }
}
