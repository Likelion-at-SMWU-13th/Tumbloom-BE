package com.tumbloom.tumblerin.global.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 리소스를 찾지 못했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다."),
    UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    ACCESS_DENIED_EXCEPTION(HttpStatus.FORBIDDEN, "해당 리소스에 대한 접근 권한이 없습니다."),
    ALREADY_EXIST_SUBJECT_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서버가 일시적으로 사용 불가 상태입니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}

