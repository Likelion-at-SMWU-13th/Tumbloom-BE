package com.tumbloom.tumblerin.app.controller;

import com.tumbloom.tumblerin.app.dto.Cafedto.CafeBatchCreateRequestDTO;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeCreateRequestDTO;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeDetailResponseDTO;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeListResponseDTO;
import com.tumbloom.tumblerin.app.security.CustomUserDetails;
import com.tumbloom.tumblerin.app.service.CafeService;
import com.tumbloom.tumblerin.global.dto.ApiResponseTemplate;
import com.tumbloom.tumblerin.global.dto.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{cafeId}")
    public ResponseEntity<?> getCafeDetail(@PathVariable Long cafeId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        CafeDetailResponseDTO cafeDetailResponseDTO = cafeService.getCafeDetail(cafeId, userId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, cafeDetailResponseDTO);
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyCafeList(@RequestParam("lat") double latitude, @RequestParam("lng") double longitude, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        List<CafeListResponseDTO> nearbyCafeList = cafeService.getNearbyCafeList(longitude, latitude, userId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, nearbyCafeList);
    }

}