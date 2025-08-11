package com.tumbloom.tumblerin.app.domain.Preference;

import com.tumbloom.tumblerin.global.exception.BusinessException;
import com.tumbloom.tumblerin.global.dto.ErrorCode;

public enum ExtraOption {
    FRANCHISE,
    PET_FRIENDLY,
    OUTDOOR_TERRACE,
    ECO_LOCAL,
    UNIQUE_THEME;

    public static ExtraOption fromString(String value) {
        try {
            return ExtraOption.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "잘못된 옵션 값입니다.");
        }
    }
}
