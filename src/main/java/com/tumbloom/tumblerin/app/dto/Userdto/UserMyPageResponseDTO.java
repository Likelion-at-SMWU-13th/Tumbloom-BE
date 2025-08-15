package com.tumbloom.tumblerin.app.dto.Userdto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMyPageResponseDTO {
    private String nickname;
    private int level;
    private int remainingSteps;
    private int tumblerUsageCount;
    private int couponIssuedCount;
    private int availableCoupons;
    private int favoriteCafeCount;
}
