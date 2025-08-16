package com.tumbloom.tumblerin.app.service;

import com.tumbloom.tumblerin.app.domain.Cafe;
import com.tumbloom.tumblerin.app.domain.Favorite;
import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.dto.Userdto.UserFavoriteCafeDTO;
import com.tumbloom.tumblerin.app.dto.Userdto.UserHomeInfoDTO;
import com.tumbloom.tumblerin.app.dto.Userdto.UserMyPageResponseDTO;
import com.tumbloom.tumblerin.app.repository.CouponRepository;
import com.tumbloom.tumblerin.app.repository.FavoriteRepository;
import com.tumbloom.tumblerin.app.repository.StampRepository;
import com.tumbloom.tumblerin.app.repository.UserRepository;
import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final StampRepository stampRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional(readOnly = true)
    public UserMyPageResponseDTO getUserInfo(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, null));

        int tumblerUsageCount = stampRepository.countByUserId(userId);
        int couponIssuedCount = couponRepository.countByUserId(userId);
        int availableCoupons = couponRepository.countByUserIdAndIsUsedFalse(userId);
        int favoriteCafeCount = favoriteRepository.countByUserId(userId);
        String levelName = getLevelName(tumblerUsageCount);
        int remaining = remainingToNextLevel(tumblerUsageCount);


        return UserMyPageResponseDTO.builder()
                .nickname(user.getNickname())
                .level(levelName)
                .remainingSteps(remaining)
                .tumblerUsageCount(tumblerUsageCount)
                .couponIssuedCount(couponIssuedCount)
                .availableCoupons(availableCoupons)
                .favoriteCafeCount(favoriteCafeCount)
                .build();

    }

    @Transactional(readOnly = true)
    public UserHomeInfoDTO getUserHomeInfo(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, null));

        int totalStampCount = stampRepository.countByUserId(userId);
        int issuedCoupons = couponRepository.countByUserId(userId);


        /*
        쿠폰 발급후 남은 가용 stamp 수 계산
         */
        int availableStampCount = totalStampCount - (issuedCoupons * 8);
        availableStampCount = Math.max(availableStampCount, 0);

        /*
        도장판에 찍힐 stamp 수
         */
        int currentStampCount = availableStampCount % 8;
        if(currentStampCount == 0 && availableStampCount > 0){
            currentStampCount = 8; // 딱 8의 배수일 때 도장판 꽉 차게 표시
        }

         /*
        쿠폰 교환 팝업 띄우기 여부 플래그 값
         */
        boolean canExchangeCoupon = availableStampCount >= 8;

        /*
        가용 도장 및 도장판 현황 요약 : N/8
         */
        String stampSummary = availableStampCount + "/8";

        UserHomeInfoDTO homeInfoDTO = new UserHomeInfoDTO();

        // WelcomeStatusDTO
        UserHomeInfoDTO.WelcomeStatusDTO welcomeStatus = homeInfoDTO.new WelcomeStatusDTO();
        welcomeStatus.setUserNickname(user.getNickname());
        welcomeStatus.setTumblerUsageCount(String.format("%02d회", totalStampCount));
        welcomeStatus.setWaterSavedLiter(String.format("%.2fL", totalStampCount * 0.55));
        welcomeStatus.setTreeSavedCount(String.format("%.3f 그루", totalStampCount * 0.003));

        // StampStatusDTO 구성
        UserHomeInfoDTO.StampStatusDTO stampStatus = homeInfoDTO.new StampStatusDTO();
        stampStatus.setCurrentStampCount(currentStampCount);
        stampStatus.setCanExchangeCoupon(canExchangeCoupon);
        stampStatus.setStampSummary(stampSummary);

        homeInfoDTO.setWelcomeStatus(welcomeStatus);
        homeInfoDTO.setStampStatus(stampStatus);

        return homeInfoDTO;
    }

    @Transactional(readOnly = true)
    public List<UserFavoriteCafeDTO> getFavoriteCafes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, null));

        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return favorites.stream()
                .map(fav -> {
                    Cafe cafe = fav.getCafe();
                    return UserFavoriteCafeDTO.builder()
                            .id(cafe.getId())
                            .cafeName(cafe.getCafeName())
                            .imageUrl(cafe.getImageUrl())
                            .address(cafe.getAddress())
                            .businessHours(cafe.getBusinessHours())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public static String getLevelName(int stampCount) {
        if (stampCount <= 4) return "Lv1. 텀블러 뉴비";
        else if (stampCount <= 10) return "Lv2. 텀블러 입문자";
        else if (stampCount <= 20) return "Lv3. 텀블러 러버";
        else if (stampCount <= 40) return "Lv4. 텀블러 고수";
        else return "Lv5. 텀블러 히어로";
    }

    public static int remainingToNextLevel(int stampCount) {
        if (stampCount <= 4) return 5 - stampCount;
        else if (stampCount <= 10) return 11 - stampCount;
        else if (stampCount <= 20) return 21 - stampCount;
        else if (stampCount <= 40) return 41 - stampCount;
        else return 0;
    }

    public static int getMinStampsForLevel(String level) {
        return switch(level) {
            case "Lv1. 텀블러 뉴비" -> 0;
            case "Lv2. 텀블러 입문자" -> 5;
            case "Lv3. 텀블러 러버" -> 11;
            case "Lv4. 텀블러 고수" -> 21;
            case "Lv5. 텀블러 히어로" -> 41;
            default -> 0;
        };
    }

    public static int getMaxStampsForLevel(String level) {
        return switch(level) {
            case "Lv1. 텀블러 뉴비" -> 5;
            case "Lv2. 텀블러 입문자" -> 11;
            case "Lv3. 텀블러 러버" -> 21;
            case "Lv4. 텀블러 고수" -> 41;
            case "Lv5. 텀블러 히어로" -> 41;  // 마지막 레벨은 고정
            default -> 0;
        };
    }


}
