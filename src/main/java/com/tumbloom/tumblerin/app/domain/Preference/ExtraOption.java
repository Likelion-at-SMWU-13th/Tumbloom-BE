package com.tumbloom.tumblerin.app.domain.Preference;

import com.tumbloom.tumblerin.global.exception.BusinessException;
import com.tumbloom.tumblerin.global.dto.ErrorCode;

public enum ExtraOption {
    FRANCHISE("브랜드/프랜차이즈"),
    PET_FRIENDLY("반려동물 가능"),
    OUTDOOR_TERRACE("야외/테라스"),
    ECO_LOCAL("친환경/로컬"),
    UNIQUE_THEME("이색테마/메뉴");

    private final String korean;

    ExtraOption(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }

    public static ExtraOption fromKorean(String korean) {
        for (ExtraOption option : values()) {
            if (option.korean.equals(korean)) {
                return option;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_REQUEST, "잘못된 한글 라벨입니다.");
    }
}
