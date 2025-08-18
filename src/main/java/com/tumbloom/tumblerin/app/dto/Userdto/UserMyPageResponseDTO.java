package com.tumbloom.tumblerin.app.dto.Userdto;

import lombok.Builder;
import lombok.Data;


import java.util.List;

@Data
@Builder
public class UserMyPageResponseDTO {
    private String nickname;
    private String level;
    private int stepsLeft;
    private int tumblerCount;
    private int issuedCoupons;
    private int availableCoupons;
    private int favoriteCafes;

    private List<String> topPreferences;
    private double levelProgress;

}
