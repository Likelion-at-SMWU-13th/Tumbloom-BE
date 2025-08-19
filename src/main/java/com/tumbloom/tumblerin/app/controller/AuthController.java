package com.tumbloom.tumblerin.app.controller;

import com.tumbloom.tumblerin.app.dto.Authdto.*;
import com.tumbloom.tumblerin.app.repository.RefreshTokenRepository;
import com.tumbloom.tumblerin.app.service.UserService;
import com.tumbloom.tumblerin.global.dto.ApiResponseTemplate;
import com.tumbloom.tumblerin.global.dto.SuccessCode;
import com.tumbloom.tumblerin.global.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "회원가입, 로그인, 로그아웃, 토큰 재발급 API")
public class AuthController {

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "신규 사용자를 등록합니다.\n\n"
            + "- 이메일 형식을 반드시 준수하여 주세요.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일로 인한 회원가입 실패")
    })
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDTO requestDto) {
        userService.signup(requestDto);
        return ApiResponseTemplate.success(SuccessCode.USER_CREATED, "회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 Access/Refresh 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 비밀번호 및 이메일)"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        TokenResponseDTO response= userService.login(request);
        return ApiResponseTemplate.success(SuccessCode.LOGIN_SUCCESSFUL, response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "Refresh Token을 무효화시켜 로그아웃합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "Refresh Token 불일치 / 만료 / 위조"),
            @ApiResponse(responseCode = "404", description = "저장된 Refresh Token을 찾을 수 없음")
    })
    public ResponseEntity<?> logout(@Valid @RequestBody RefreshRequestDTO request) {
        userService.logout(request.getRefreshToken());
        return ApiResponseTemplate.success(SuccessCode.LOGOUT_SUCCESSFUL, null);
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "Refresh Token을 이용해 새로운 Access Token을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "Refresh Token 불일치 / 만료 / 위조"),
            @ApiResponse(responseCode = "404", description = "저장된 Refresh Token을 찾을 수 없음")
    })
    public ResponseEntity<?> refresh(@RequestBody RefreshRequestDTO request) {
        TokenResponseDTO response = userService.refresh(request.getRefreshToken());
        return ApiResponseTemplate.success(SuccessCode.TOKEN_REFRESHED, response);
    }

}
