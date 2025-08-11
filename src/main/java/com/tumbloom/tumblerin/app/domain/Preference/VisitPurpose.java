package com.tumbloom.tumblerin.app.domain.Preference;

import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;

public enum VisitPurpose {
    EMOTIONAL_ATMOSPHERE,
    STUDY_WORKSPACE,
    CHAT_MEETING,
    HOT_PLACE;

    public static ExtraOption fromString(String value) {
        try {
            return ExtraOption.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "잘못된 옵션 값입니다.");
        }
    }
}
