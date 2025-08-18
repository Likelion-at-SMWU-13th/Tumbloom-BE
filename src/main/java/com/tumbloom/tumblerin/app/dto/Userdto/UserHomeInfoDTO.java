package com.tumbloom.tumblerin.app.dto.Userdto;

import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class UserHomeInfoDTO {
    private WelcomeStatusDTO welcomeStatus;
    private StampStatusDTO stampStatus;


    @Data
    @Builder
    public static class WelcomeStatusDTO {
        private String nickname;
        private String tumblerCount;
        private String savedWater;
        private String savedTree;
    }
    @Data
    @Builder
    public static class StampStatusDTO {
        private String Summary;
        private int currentCount;
        private boolean isExchangeable;
    }
}


