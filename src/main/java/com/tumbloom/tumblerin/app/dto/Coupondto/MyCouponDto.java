package com.tumbloom.tumblerin.app.dto.Coupondto;


import com.tumbloom.tumblerin.app.domain.Coupon;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyCouponDto {
    private Long couponId;
    private String cafeName;
    private String expiredDate;
    private boolean used;

    public static MyCouponDto from(Coupon c) {
        return MyCouponDto.builder()
                .couponId(c.getId())
                .cafeName(c.getCafeName())
                .expiredDate(c.getExpiredDate())
                .used(Boolean.TRUE.equals(c.getIsUsed()))
                .build();
    }
}
