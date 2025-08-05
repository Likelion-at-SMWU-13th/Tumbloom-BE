package com.tumbloom.tumblerin.global.dto;

import lombok.*;
import org.springframework.http.ResponseEntity;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ApiResponseTemplate<T> {
    private final int status;
    private final boolean success;
    private final String message;
    private T data;

    public static <T> ResponseEntity<ApiResponseTemplate<T>> success(SuccessCode successCode, T data) {
        return ResponseEntity.ok(ApiResponseTemplate.<T>builder()
                .status(successCode.getHttpStatus().value())
                .success(true)
                .message(successCode.getMessage())
                .data(data)
                .build());
    }

    public static <T> ResponseEntity<ApiResponseTemplate<T>> error(ErrorCode errorCode, T data) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponseTemplate.<T>builder()
                        .status(errorCode.getHttpStatus().value())
                        .success(false)
                        .message(errorCode.getMessage())
                        .data(data)
                        .build());
    }
}

