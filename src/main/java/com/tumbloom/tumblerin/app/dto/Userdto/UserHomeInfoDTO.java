package com.tumbloom.tumblerin.app.dto.Userdto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserHomeInfoDTO {
    private WelcomeStatusDTO welcomeStatus;
    private StampStatusDTO stampStatus;


    @Setter
    @Getter
    public class WelcomeStatusDTO {
        private String userNickname;
        private String tumblerUsageCount;
        private String waterSavedLiter;
        private String treeSavedCount;
    }

    @Setter
    @Getter
    public class StampStatusDTO {
        private String stampSummary;
        private int currentStampCount;
        private boolean canExchangeCoupon;
    }
}


