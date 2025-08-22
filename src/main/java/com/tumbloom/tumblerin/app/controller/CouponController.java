package com.tumbloom.tumblerin.app.controller;

import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.dto.Coupondto.AvailableCafeCouponDto;
import com.tumbloom.tumblerin.app.dto.Coupondto.MyCouponDetailResponse;
import com.tumbloom.tumblerin.app.dto.Coupondto.MyCouponListResponse;
import com.tumbloom.tumblerin.app.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CouponController {

    private final CouponService couponService;

    @Operation(
            summary = "교환 가능한 카페 목록 조회",
            description = """
                - (선택) lat/lng가 있으면: 내 주변 거리순 TOP7만 반환
                - 없으면: 기존 검색/전체 조회
                - 각 항목은 남은 수량과 할인가(discountPrice)를 포함
                """
    )
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공") })
    @GetMapping("/coupons")
    public List<AvailableCafeCouponDto> listAvailable(
            @Parameter(description = "검색할 카페명(부분 일치)") @RequestParam(required = false) String cafeName,
            @Parameter(description = "위도") @RequestParam(required = false) Double lat,
            @Parameter(description = "경도") @RequestParam(required = false) Double lng
    ) {
        return couponService.listAvailableCafes(cafeName, lat, lng);
    }

    @Operation(summary = "쿠폰 발급", description = "특정 카페에서 쿠폰을 발급받습니다. 쿠폰은 최대 20개까지만 발급 가능하며, 발급 시 해당 카페의 남은 수량이 1 감소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공"),
            @ApiResponse(responseCode = "404", description = "카페 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "쿠폰이 모두 소진됨")
    })
    @PostMapping("/cafes/{cafeId}/coupons")
    public MyCouponDetailResponse issue(
            @Parameter(description = "쿠폰을 발급받을 카페 ID") @PathVariable Long cafeId,
            @AuthenticationPrincipal(expression = "user") User loginUser
    ) {
        return couponService.issueCoupon(cafeId, loginUser);
    }

    @Operation(summary = "내 보유 쿠폰 목록 조회", description = "현재 로그인한 사용자가 보유 중인 미사용 쿠폰 목록을 반환합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "조회 성공") })
    @GetMapping("/coupons/my")
    public MyCouponListResponse myCoupons(@AuthenticationPrincipal(expression = "user") User loginUser) {
        return couponService.listMyCoupons(loginUser.getId());
    }

    @Operation(summary = "내 쿠폰 상세 조회", description = "쿠폰 ID를 기준으로, 현재 로그인한 사용자가 보유한 쿠폰의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "내 쿠폰이 아님 또는 존재하지 않음")
    })
    @GetMapping("/coupons/my/{couponId}")
    public MyCouponDetailResponse myCouponDetail(
            @Parameter(description = "상세 조회할 쿠폰 ID") @PathVariable Long couponId,
            @AuthenticationPrincipal(expression = "user") User loginUser
    ) {
        return couponService.getMyCoupon(loginUser.getId(), couponId);
    }

    @Operation(summary = "내 쿠폰 사용", description = "보유한 쿠폰을 사용 처리합니다. 실제 삭제는 아니고 isUsed=true 로 상태가 변경됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 처리 성공"),
            @ApiResponse(responseCode = "404", description = "내 쿠폰이 아님 또는 존재하지 않음"),
            @ApiResponse(responseCode = "409", description = "이미 사용된 쿠폰")
    })
    @DeleteMapping("/coupons/my/{couponId}")
    public void useMyCoupon(
            @Parameter(description = "사용 처리할 쿠폰 ID") @PathVariable Long couponId,
            @AuthenticationPrincipal(expression = "user") User loginUser
    ) {
        couponService.useMyCoupon(loginUser.getId(), couponId);
    }
}
