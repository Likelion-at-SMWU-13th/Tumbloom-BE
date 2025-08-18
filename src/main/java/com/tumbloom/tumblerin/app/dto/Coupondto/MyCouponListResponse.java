package com.tumbloom.tumblerin.app.dto.Coupondto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyCouponListResponse {
    private int usableCount;        // 사용 가능(미사용) 개수
    private List<MyCouponDto> items;
}

