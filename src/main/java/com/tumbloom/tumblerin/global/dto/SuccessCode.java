package com.tumbloom.tumblerin.global.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {

    //일반적인 성공 응답
    OPERATION_SUCCESSFUL(HttpStatus.OK, "작업이 성공적으로 처리되었습니다."),
    REQUEST_PROCESSED(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),

    // 리소스 관련
    RESOURCE_CREATED(HttpStatus.CREATED, "리소스가 성공적으로 생성되었습니다."),
    RESOURCE_UPDATED(HttpStatus.OK, "리소스가 성공적으로 업데이트되었습니다."),
    RESOURCE_DELETED(HttpStatus.NO_CONTENT, "리소스가 성공적으로 삭제되었습니다."),
    RESOURCE_RETRIEVED(HttpStatus.OK, "리소스가 성공적으로 조회되었습니다."),

    //사용자 관련
    USER_CREATED(HttpStatus.CREATED, "사용자가 회원가입에 성공하였습니다."),
    USER_UPDATED(HttpStatus.OK, "사용자 정보가 성공적으로 업데이트되었습니다."),
    USER_LOGGED_IN(HttpStatus.OK, "사용자가 성공적으로 로그인되었습니다."),
    USER_LOGGED_OUT(HttpStatus.OK, "사용자가 성공적으로 로그아웃되었습니다."),

    // 인증 관련
    LOGIN_SUCCESSFUL(HttpStatus.OK, "로그인에 성공하였습니다."),
    LOGOUT_SUCCESSFUL(HttpStatus.OK, "로그아웃에 성공하였습니다.");


    private final HttpStatus httpStatus;
    private final String message;

}