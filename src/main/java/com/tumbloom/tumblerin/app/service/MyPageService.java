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

        int level = getLevel(tumblerCount);
        int stepsLeft = remainingToNextLevel(tumblerCount);

        return UserMyPageResponseDTO.builder()
                .nickname(user.getNickname())
                .level(level)
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

        UserHomeInfoDTO.WelcomeStatusDTO welcomeStatus = UserHomeInfoDTO.WelcomeStatusDTO.builder()
                .nickname(user.getNickname())
                .tumblerCount(String.format("%02d회", totalStampCount))
                .savedWater(String.format("%.2fL", totalStampCount * 0.55))
                .savedTree(String.format("%.3f 그루", totalStampCount * 0.003))
                .build();

        UserHomeInfoDTO.StampStatusDTO stampStatus = UserHomeInfoDTO.StampStatusDTO.builder()
                .currentCount(currentStampCount)
                .exchangeable(exchangeable)
                .validStampCnt(availableStampCount)
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

    public static int getLevel(int stampCount) {
        if (stampCount <= 4) return 1; // Lv1
        else if (stampCount <= 10) return 2; // Lv2
        else if (stampCount <= 20) return 3; // Lv3
        else if (stampCount <= 40) return 4; // Lv4
        else return 5; // Lv5
    }

    public static int remainingToNextLevel(int stampCount) {
        if (stampCount <= 4) return 5 - stampCount;
        else if (stampCount <= 10) return 11 - stampCount;
        else if (stampCount <= 20) return 21 - stampCount;
        else if (stampCount <= 40) return 41 - stampCount;
        else return 0;
    }

    public static int getMinStampsForLevel(int level) {
        return switch(level) {
            case 1 -> 0;
            case 2 -> 5;
            case 3 -> 11;
            case 4 -> 21;
            case 5 -> 41;
            default -> 0;
        };
    }

    public static int getMaxStampsForLevel(int level) {
        return switch(level) {
            case 1 -> 4;
            case 2 -> 10;
            case 3 -> 20;
            case 4 -> 40;
            case 5 -> Integer.MAX_VALUE;
            default -> 0;
        };
    }


}
