package com.tumbloom.tumblerin.app.domain.Preference;

import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;

public enum PreferredMenu {
    SPECIALTY("고급/스페셜티"),
    DESSERT("디저트 맛집"),
    DECAF("디카페인"),
    SEASON_MENU("빙수/계절메뉴"),
    BRUNCH("브런치/식사");

    private final String korean;

    PreferredMenu(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }

    public static PreferredMenu fromKorean(String korean) {
        for (PreferredMenu menu : values()) {
            if (menu.korean.equals(korean)) {
                return menu;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_REQUEST, "잘못된 한글 라벨입니다.");
    }
}
