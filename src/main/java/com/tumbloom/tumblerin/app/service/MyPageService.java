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

        int tumblerCount = stampRepository.countByUserId(userId);
        int issuedCoupons = couponRepository.countByUserId(userId);
        int availableCoupons = couponRepository.countByUserIdAndIsUsedFalse(userId);
        int favoriteCafes = favoriteRepository.countByUserId(userId);

        String levelName = getLevelName(tumblerCount);
        int stepsLeft = remainingToNextLevel(tumblerCount);

        return UserMyPageResponseDTO.builder()
                .nickname(user.getNickname())
                .level(levelName)
                .stepsLeft(stepsLeft)
                .tumblerCount(tumblerCount)
                .issuedCoupons(issuedCoupons)
                .availableCoupons(availableCoupons)
                .favoriteCafes(favoriteCafes)
                .build();

    }

    @Transactional(readOnly = true)
    public UserHomeInfoDTO getUserHomeInfo(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, null));

        int totalStampCount = stampRepository.countByUserId(userId);
        int issuedCoupons = couponRepository.countByUserId(userId);


        int availableStampCount = calculateAvailableStampCount(totalStampCount, issuedCoupons);
        int currentStampCount = calculateCurrentStampCount(availableStampCount);

        boolean exchangeable = availableStampCount >= 8;

        String stampSummary = availableStampCount + "/8";

        UserHomeInfoDTO.WelcomeStatusDTO welcomeStatus = UserHomeInfoDTO.WelcomeStatusDTO.builder()
                .nickname(user.getNickname())
                .tumblerCount(String.format("%02d회", totalStampCount))
                .savedWater(String.format("%.2fL", totalStampCount * 0.55))
                .savedTree(String.format("%.3f 그루", totalStampCount * 0.003))
                .build();

        UserHomeInfoDTO.StampStatusDTO stampStatus = UserHomeInfoDTO.StampStatusDTO.builder()
                .currentCount(currentStampCount)
                .isExchangeable(exchangeable)
                .Summary(stampSummary)
                .build();

        // 최종 DTO
        return UserHomeInfoDTO.builder()
                .welcomeStatus(welcomeStatus)
                .stampStatus(stampStatus)
                .build();
    }

    private int calculateAvailableStampCount(int totalStampCount, int issuedCoupons) {
        return Math.max(totalStampCount - (issuedCoupons * 8), 0);
    }

    private int calculateCurrentStampCount(int availableStampCount) {
        int currentStampCount = availableStampCount % 8;
        return (currentStampCount == 0 && availableStampCount > 0) ? 8 : currentStampCount;
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
            case "Lv1. 텀블러 뉴비" -> 4;
            case "Lv2. 텀블러 입문자" -> 10;
            case "Lv3. 텀블러 러버" -> 20;
            case "Lv4. 텀블러 고수" -> 40;
            case "Lv5. 텀블러 히어로" -> Integer.MAX_VALUE;
            default -> 0;
        };
    }


}
