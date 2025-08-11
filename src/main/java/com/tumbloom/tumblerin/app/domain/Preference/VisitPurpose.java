package com.tumbloom.tumblerin.app.domain.Preference;

import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;

public enum VisitPurpose {
    EMOTIONAL_ATMOSPHERE("감성/분위기"),
    STUDY_WORKSPACE("공부/작업공간"),
    CHAT_MEETING("수다/모임"),
    HOT_PLACE("HOT플레이스");

    private final String korean;

    VisitPurpose(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }

    public static VisitPurpose fromKorean(String korean) {
        for (VisitPurpose purpose : values()) {
            if (purpose.korean.equals(korean)) {
                return purpose;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_REQUEST, "잘못된 한글 라벨입니다.");
    }
}
