package com.tumbloom.tumblerin.app.controller;

import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.dto.Verifydto.VerificationCodeVerifyRequestDTO;
import com.tumbloom.tumblerin.app.dto.Verifydto.VerificationCodeVerifyResponseDTO;
import com.tumbloom.tumblerin.app.security.CustomUserDetails;
import com.tumbloom.tumblerin.app.service.CafeVerificationService;
import com.tumbloom.tumblerin.global.dto.ApiResponseTemplate;
import com.tumbloom.tumblerin.global.dto.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 직원확인코드 검증 컨트롤러
 * - 코드가 일치하면 스탬프 1개 적립
 * - 불일치 또는 코드 미설정 시 에러 처리(서비스에서 BusinessException)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CafeVerificationController {

    private final CafeVerificationService cafeVerificationService;

    @Operation(
            summary = "직원확인코드 검증(+성공 시 스탬프 적립)",
            description = """
                직원이 입력한 확인코드가 해당 카페의 코드와 일치하는지 검증합니다.
                - 일치하면 스탬프 1개를 적립하고 valid=true 를 반환합니다.
                - 불일치이거나, 해당 카페에 코드가 없으면 400(INVALID_REQUEST) 에러를 반환합니다.
                요청 예시:
                POST /api/cafes/{cafeId}/verification-code/verify
                {
                  "code": "ABC1234"
                }
                """
    )
    @PostMapping(
            value = "/cafes/{cafeId}/verification-code/verify",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> verify(
            @Parameter(description = "카페 ID") @PathVariable Long cafeId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody VerificationCodeVerifyRequestDTO request
    ) {
        User loginUser = userDetails.getUser();
        VerificationCodeVerifyResponseDTO dto =
                cafeVerificationService.verifyAndStamp(cafeId, request.getCode(), loginUser);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_RETRIEVED, dto);
    }
}
