package com.tumbloom.tumblerin.app.service;


import com.tumbloom.tumblerin.app.domain.Cafe;
import com.tumbloom.tumblerin.app.domain.Stamp;
import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.dto.Verifydto.VerificationCodeVerifyResponseDTO;
import com.tumbloom.tumblerin.app.repository.CafeRepository;
import com.tumbloom.tumblerin.app.repository.StampRepository;
import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CafeVerificationService {

    private final CafeRepository cafeRepository;
    private final StampRepository stampRepository;

    /** 직원확인코드 검증 + 성공 시 스탬프 1개 적립 */
    @Transactional
    public VerificationCodeVerifyResponseDTO verifyAndStamp(Long cafeId, String inputCode, User user) {
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "카페를 찾을 수 없습니다. id=" + cafeId));

        boolean valid = (cafe.getVerificationCode() != null)
                && cafe.getVerificationCode().equalsIgnoreCase(
                inputCode == null ? "" : inputCode.trim()
        );

        if (!valid) {
            // 유효하지 않으면 에러 반환 (요청사항)
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "확인코드가 일치하지 않습니다.");
        }

        //  유효하면 스탬프 1개 적립
        Stamp stamp = Stamp.builder()
                .user(user)
                .cafe(cafe)
                .build();
        stampRepository.save(stamp);

        return new VerificationCodeVerifyResponseDTO(true);
    }
}
