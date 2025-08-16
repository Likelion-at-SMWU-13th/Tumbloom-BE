package com.tumbloom.tumblerin.app.controller;

import com.tumbloom.tumblerin.app.dto.Cafedto.CafeBatchCreateRequestDTO;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeCreateRequestDTO;
import com.tumbloom.tumblerin.app.service.CafeService;
import com.tumbloom.tumblerin.global.dto.ApiResponseTemplate;
import com.tumbloom.tumblerin.global.dto.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cafes")
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;

    @PostMapping
    public  ResponseEntity<?> createCafe(@RequestBody CafeCreateRequestDTO request) {
        cafeService.createCafe(request);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_CREATED, "카페 정보가 성공적으로 등록되었습니다.");
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createCafeBatch(@RequestBody CafeBatchCreateRequestDTO request) {
        cafeService.createCafeBatch(request);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_CREATED, "카페들의 정보가 성공적으로 등록되었습니다.");
    }
}