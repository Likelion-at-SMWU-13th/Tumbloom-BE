package com.tumbloom.tumblerin.app.dto.Coupondto;

import com.tumbloom.tumblerin.app.domain.Cafe;
import com.tumbloom.tumblerin.app.domain.CouponManager;
import lombok.Builder;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Getter
@Builder
public class AvailableCafeCouponDto {
    private Long cafeId;
    private String cafeName;
    private String imageUrl;
    private String address;
    private String businessHours;
    private double latitude;      // from location.getY()
    private double longitude;     // from location.getX()
    private int availableCount;
    private int discountPrice;    // (목록 표기용) 필요 없으면 지워도 됨

    /** 기본 팩토리: 위치를 Point에서 lat/lng로 변환 */
    public static AvailableCafeCouponDto from(CouponManager cm) {
        Cafe cafe = cm.getCafe();
        Point loc = cafe.getLocation();

        double lat = 0.0;
        double lng = 0.0;
        if (loc != null) {
            lat = loc.getY(); // latitude
            lng = loc.getX(); // longitude
        }

        return AvailableCafeCouponDto.builder()
                .cafeId(cafe.getId())
                .cafeName(cafe.getCafeName())
                .imageUrl(cafe.getImageUrl())
                .address(cafe.getAddress())
                .businessHours(cafe.getBusinessHours())
                .latitude(lat)
                .longitude(lng)
                .availableCount(cm.getCouponQuantity())
                .discountPrice(1000) // 정책값/표기값. 필요 시 서비스에서 세팅
                .build();
    }

    /** (선택) 서비스에서 표기용 할인가를 주입하고 싶을 때 쓰는 팩토리 */
    public static AvailableCafeCouponDto from(CouponManager cm, int listDiscountPrice) {
        Cafe cafe = cm.getCafe();
        Point loc = cafe.getLocation();

        double lat = 0.0;
        double lng = 0.0;
        if (loc != null) {
            lat = loc.getY();
            lng = loc.getX();
        }

        return AvailableCafeCouponDto.builder()
                .cafeId(cafe.getId())
                .cafeName(cafe.getCafeName())
                .imageUrl(cafe.getImageUrl())
                .address(cafe.getAddress())
                .businessHours(cafe.getBusinessHours())
                .latitude(lat)
                .longitude(lng)
                .availableCount(cm.getCouponQuantity())
                .discountPrice(listDiscountPrice)
                .build();
    }
}
