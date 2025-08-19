package com.tumbloom.tumblerin.app.controller;

import com.tumbloom.tumblerin.app.dto.Cafedto.CafeBatchCreateRequestDTO;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeCreateRequestDTO;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeDetailResponseDTO;
import com.tumbloom.tumblerin.app.dto.Cafedto.CafeListResponseDTO;
import com.tumbloom.tumblerin.app.security.CustomUserDetails;
import com.tumbloom.tumblerin.app.service.CafeRecommendationMappingService;
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
    private final CafeRecommendationMappingService cafeRecommendationMappingService;

    @PostMapping
    public ResponseEntity<ApiResponseTemplate<String>> createCafe(
            @RequestBody CafeCreateRequestDTO request
    ) {
        cafeService.createCafe(request);
        return ApiResponseTemplate.success(
                SuccessCode.RESOURCE_CREATED,
                "카페 정보가 성공적으로 등록되었습니다."
        );
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponseTemplate<String>> createCafeBatch(
            @RequestBody CafeBatchCreateRequestDTO request
    ) {
        cafeService.createCafeBatch(request);
        return ApiResponseTemplate.success(
                SuccessCode.RESOURCE_CREATED,
                "카페들의 정보가 성공적으로 등록되었습니다."
        );
    }

    @GetMapping("/{cafeId}")
    public ResponseEntity<ApiResponseTemplate<CafeDetailResponseDTO>> getCafeDetail(
            @PathVariable Long cafeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        CafeDetailResponseDTO dto = cafeService.getCafeDetail(cafeId, userId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, dto);
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponseTemplate<List<CafeListResponseDTO>>> getNearbyCafeList(
            @RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<CafeListResponseDTO> list = cafeService.getNearbyCafeList(longitude, latitude, userId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, list);
    }

    @GetMapping("/nearby/top")
    public ResponseEntity<ApiResponseTemplate<List<CafeListResponseDTO>>> getNearbyTop5CafeList(
            @RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<CafeListResponseDTO> list = cafeService.getNearbyTop5CafeList(longitude, latitude, userId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, list);
    }

    @GetMapping
    public ResponseEntity<ApiResponseTemplate<List<CafeListResponseDTO>>> searchByKeyword(
            @RequestParam("keyword") String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<CafeListResponseDTO> list = cafeService.searchByKeyword(keyword, userId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, list);
    }

    @GetMapping("/filtered/ai")
    public ResponseEntity<ApiResponseTemplate<List<CafeListResponseDTO>>> getFilteredByAI(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<CafeListResponseDTO> list = cafeRecommendationMappingService.getRecommendCafeList(userId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, list);
    }

    @GetMapping("/filtered/coupon")
    public ResponseEntity<ApiResponseTemplate<List<CafeListResponseDTO>>> getFilteredByCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<CafeListResponseDTO> list = cafeService.getFilteredByCoupon(userId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, list);
    }

    @GetMapping("/filtered/popular")
    public ResponseEntity<ApiResponseTemplate<List<CafeListResponseDTO>>> getFilteredByPopular(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<CafeListResponseDTO> list = cafeService.getFilteredByPopular(userId);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, list);
    }
}
