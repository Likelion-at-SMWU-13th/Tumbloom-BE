package com.tumbloom.tumblerin.app.service;

import com.tumbloom.tumblerin.app.domain.Cafe;
import com.tumbloom.tumblerin.app.domain.Coupon;
import com.tumbloom.tumblerin.app.domain.CouponManager;
import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.dto.Coupondto.AvailableCafeCouponDto;
import com.tumbloom.tumblerin.app.dto.Coupondto.MyCouponDetailResponse;
import com.tumbloom.tumblerin.app.dto.Coupondto.MyCouponDto;
import com.tumbloom.tumblerin.app.dto.Coupondto.MyCouponListResponse;
import com.tumbloom.tumblerin.app.repository.CafeRepository;
import com.tumbloom.tumblerin.app.repository.CouponManagerRepository;
import com.tumbloom.tumblerin.app.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponManagerRepository couponManagerRepository;
    private final CafeRepository cafeRepository;

    private static final int MAX_COUPON_PER_CAFE = 20;
    private static final DateTimeFormatter EXP_FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Transactional(readOnly = true)
    public List<AvailableCafeCouponDto> listAvailableCafes(String cafeName) {
        var managers = (cafeName == null || cafeName.isBlank())
                ? couponManagerRepository.findAllAvailable()
                : couponManagerRepository.searchAvailableByCafeName(cafeName.trim());
        return managers.stream().map(AvailableCafeCouponDto::from).toList();
    }

    /**
     * 쿠폰 발급: /api/cafes/{cafeId}/coupons
     * - 남은 수량(0 초과) 확인 후 1 감소
     * - 쿠폰 생성 (기본 만료일: 오늘+6개월)
     */
    @Transactional
    public MyCouponDetailResponse issueCoupon(Long cafeId, User loginUser) {
        // 락을 걸고 가져온다 (동시성 제어)
        CouponManager cm = couponManagerRepository.findByCafeIdForUpdate(cafeId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "해당 카페의 쿠폰 발급정보가 없습니다."));

        // 안전장치: 최대 20장 제한 보장
        if (cm.getCouponQuantity() <= 0) {
            throw new ResponseStatusException(CONFLICT, "해당 카페의 쿠폰이 모두 소진되었습니다.");
        }
        if (cm.getCouponQuantity() > MAX_COUPON_PER_CAFE) {
            throw new ResponseStatusException(CONFLICT, "쿠폰 수량이 비정상적입니다.");
        }

        Cafe cafe = cm.getCafe();

        // 수량 차감
        // (엔터티 필드가 private 이므로 리플렉션 없이 세터가 없지만, 롬복 @Builder만 있습니다.
        //  편의상 엔터티 메서드 추가가 없다면, 아래처럼 새로운 값으로 교체하는 방식 대신,
        //  서비스에서 강제로 필드를 변경해야 합니다. JPA는 같은 영속 엔티티에서 필드 변경을 추적합니다.)
        // => cm.couponQuantity = cm.getCouponQuantity() - 1;  // 필드가 private 이라 직접 접근 불가
        setCouponManagerQuantity(cm, cm.getCouponQuantity() - 1);

        // 쿠폰 생성
        Coupon coupon = Coupon.builder()
                .couponManager(cm)
                .user(loginUser)
                .cafeName(cafe.getCafeName())
                .expiredDate(LocalDate.now().plusMonths(6).format(EXP_FMT)) // UI: "유효기간 2025.12.01" 형태
                .isUsed(false)
                .content("1000원") // 필요 시 정책에 맞게 수정
                .build();
        couponRepository.save(coupon);

        return MyCouponDetailResponse.from(coupon);
    }

    // 리플렉션 없이 수량 변경을 위한 헬퍼 (동일 패키지 내이므로 접근 제어자 주의)
    private void setCouponManagerQuantity(CouponManager cm, int newQty) {
        try {
            var f = CouponManager.class.getDeclaredField("couponQuantity");
            f.setAccessible(true);
            f.setInt(cm, newQty);
        } catch (Exception e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "쿠폰 수량 갱신 중 오류", e);
        }
    }

    @Transactional(readOnly = true)
    public MyCouponListResponse listMyCoupons(Long userId) {
        List<MyCouponDto> items = couponRepository.findByUser_IdAndIsUsedFalseOrderByIdDesc(userId)
                .stream().map(MyCouponDto::from).toList();
        return MyCouponListResponse.builder()
                .usableCount(items.size())
                .items(items)
                .build();
    }

    @Transactional(readOnly = true)
    public MyCouponDetailResponse getMyCoupon(Long userId, Long couponId) {
        Coupon c = couponRepository.findByIdAndUser_Id(couponId, userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "쿠폰을 찾을 수 없습니다."));
        return MyCouponDetailResponse.from(c);
    }

    /**
     * 쿠폰 사용(DELETE) - 실제 삭제하지 않고 isUsed=true 로 전환
     */
    @Transactional
    public void useMyCoupon(Long userId, Long couponId) {
        Coupon c = couponRepository.findByIdAndUser_Id(couponId, userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "쿠폰을 찾을 수 없습니다."));
        if (Boolean.TRUE.equals(c.getIsUsed())) {
            throw new ResponseStatusException(CONFLICT, "이미 사용된 쿠폰입니다.");
        }
        setCouponUsed(c, true);
    }

    private void setCouponUsed(Coupon coupon, boolean used) {
        try {
            var f = Coupon.class.getDeclaredField("isUsed");
            f.setAccessible(true);
            f.set(coupon, used);
        } catch (Exception e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "쿠폰 사용 처리 중 오류", e);
        }
    }
}

