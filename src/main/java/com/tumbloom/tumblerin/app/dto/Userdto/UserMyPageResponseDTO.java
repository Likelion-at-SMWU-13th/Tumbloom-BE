package com.tumbloom.tumblerin.app.dto.Userdto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserMyPageResponseDTO {
    private String nickname;
    private String level;
    private int remainingSteps;
    private int tumblerUsageCount;
    private int couponIssuedCount;
    private int availableCoupons;
    private int favoriteCafeCount;

    private List<String> topPreferences;
    private double levelProgress;

}
