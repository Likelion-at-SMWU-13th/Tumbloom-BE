package com.tumbloom.tumblerin.app.service;

import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.dto.Userdto.UserMyPageResponseDTO;
import com.tumbloom.tumblerin.app.repository.CouponRepository;
import com.tumbloom.tumblerin.app.repository.FavoriteRepository;
import com.tumbloom.tumblerin.app.repository.StampRepository;
import com.tumbloom.tumblerin.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final StampRepository stampRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional(readOnly = true)
    public UserMyPageResponseDTO getUserInfo(Long userId){

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
