package com.tumbloom.tumblerin.app.dto.Coupondto;

import com.tumbloom.tumblerin.app.domain.Cafe;
import com.tumbloom.tumblerin.app.domain.CouponManager;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AvailableCafeCouponDto {
    private Long cafeId;
    private String cafeName;
    private String imageUrl;
    private String address;
    private String businessHours;
    private int remainingQuantity; // 남은 발급 가능 수량 (최대 20)

    public static AvailableCafeCouponDto from(CouponManager cm) {
        Cafe c = cm.getCafe();
        return AvailableCafeCouponDto.builder()
                .cafeId(c.getId())
                .cafeName(c.getCafeName())
                .imageUrl(c.getImageUrl())
                .address(c.getAddress())
                .businessHours(c.getBusinessHours())
                .remainingQuantity(cm.getCouponQuantity())
                .build();
    }
}
