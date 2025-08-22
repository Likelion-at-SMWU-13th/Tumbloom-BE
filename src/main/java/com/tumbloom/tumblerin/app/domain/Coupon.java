package com.tumbloom.tumblerin.app.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "couponmanager_id", nullable = false)
    private CouponManager couponManager;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private String cafeName;

    @Column
    private String expiredDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isUsed = false;

    @Column
    private String content;

    @Column
    private Integer discountPrice;    // 정수값 (1000~

    private String imageUrl;      // 카페 이미지
}
