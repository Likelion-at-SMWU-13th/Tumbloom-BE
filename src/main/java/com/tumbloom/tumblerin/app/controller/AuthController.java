package com.tumbloom.tumblerin.app.controller;

import com.tumbloom.tumblerin.app.dto.Authdto.*;
import com.tumbloom.tumblerin.app.repository.RefreshTokenRepository;
import com.tumbloom.tumblerin.app.service.UserService;
import com.tumbloom.tumblerin.global.dto.ApiResponseTemplate;
import com.tumbloom.tumblerin.global.dto.SuccessCode;
import com.tumbloom.tumblerin.global.security.JwtTokenProvider;
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
public class AuthController {

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDTO requestDto) {
        userService.signup(requestDto);
        return ApiResponseTemplate.success(SuccessCode.USER_CREATED, "회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        TokenResponseDTO response= userService.login(request);
        return ApiResponseTemplate.success(SuccessCode.LOGIN_SUCCESSFUL, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody RefreshRequestDTO request) {
        userService.logout(request.getRefreshToken());
        return ApiResponseTemplate.success(SuccessCode.LOGOUT_SUCCESSFUL, null);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequestDTO request) {
        TokenResponseDTO response = userService.refresh(request.getRefreshToken());
        return ApiResponseTemplate.success(SuccessCode.TOKEN_REFRESHED, response);
    }

}
