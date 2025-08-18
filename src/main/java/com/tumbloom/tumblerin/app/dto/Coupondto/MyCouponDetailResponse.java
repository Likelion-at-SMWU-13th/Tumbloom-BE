package com.tumbloom.tumblerin.app.dto.Coupondto;


import com.tumbloom.tumblerin.app.domain.Coupon;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyCouponDetailResponse {
    private Long couponId;
    private String cafeName;
    private String content;      // 할인내용(예: 1,000원)
    private String expiredDate;
    private boolean used;

    public static MyCouponDetailResponse from(Coupon c) {
        return MyCouponDetailResponse.builder()
                .couponId(c.getId())
                .cafeName(c.getCafeName())
                .content(c.getContent())
                .expiredDate(c.getExpiredDate())
                .used(Boolean.TRUE.equals(c.getIsUsed()))
                .build();
    }
}
