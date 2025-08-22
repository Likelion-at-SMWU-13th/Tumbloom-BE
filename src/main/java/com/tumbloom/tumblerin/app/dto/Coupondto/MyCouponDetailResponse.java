package com.tumbloom.tumblerin.app.dto.Coupondto;

import com.tumbloom.tumblerin.app.domain.Coupon;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyCouponDetailResponse {
    private Long couponId;
    private String cafeName;
    private String content;
    private String expiredDate;
    private boolean used;
    private int discountPrice;
    private String imageUrl;

    public static MyCouponDetailResponse from(Coupon c) {
        return MyCouponDetailResponse.builder()
                .couponId(c.getId())
                .cafeName(c.getCafeName())
                .content(c.getContent())
                .expiredDate(c.getExpiredDate())
                .used(Boolean.TRUE.equals(c.getIsUsed()))
                .discountPrice(c.getDiscountPrice())
                .imageUrl(c.getImageUrl())
                .build();
    }
}
