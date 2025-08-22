package com.tumbloom.tumblerin.app.service;

import com.tumbloom.tumblerin.app.domain.RefreshToken;
import com.tumbloom.tumblerin.app.repository.RefreshTokenRepository;
import com.tumbloom.tumblerin.app.repository.UserRepository;
import com.tumbloom.tumblerin.app.domain.RoleType;
import com.tumbloom.tumblerin.app.domain.User;
import com.tumbloom.tumblerin.app.dto.Authdto.LoginRequestDTO;
import com.tumbloom.tumblerin.app.dto.Authdto.TokenResponseDTO;
import com.tumbloom.tumblerin.app.dto.Authdto.SignupRequestDTO;
import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;
import com.tumbloom.tumblerin.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public void signup(SignupRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_EXIST_SUBJECT_EXCEPTION, "이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roleType(RoleType.USER)
                .build();

        userRepository.save(user);
    }

    public TokenResponseDTO login(LoginRequestDTO request) {

        userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,"해당 이메일은 존재하지 않습니다."));

        // 2️⃣ 비밀번호 검증
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "해당 이메일 계정의 비밀번호가 올바르지 않습니다."); // 401
        }


        String email = request.getEmail();
        String accessToken = jwtTokenProvider.createAccessToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        RefreshToken tokenEntity = RefreshToken.builder()
                .email(email)
                .refreshToken(refreshToken)
                .expiryDate(new Date())
                .build();

        refreshTokenRepository.save(tokenEntity);

        return new TokenResponseDTO(accessToken, refreshToken);
    }

    // 로그아웃
    public void logout(String refreshToken) {

        jwtTokenProvider.validateRefreshToken(refreshToken);

        String email = jwtTokenProvider.getUserEmailFromToken(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND,
                        "로그아웃 대상 Refresh Token이 존재하지 않습니다."));
        if (!storedToken.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISMATCH,
                    "Refresh Token이 일치하지 않아 로그아웃할 수 없습니다.");
        }
        refreshTokenRepository.delete(storedToken);
    }

    // 토큰 재발급
    public TokenResponseDTO refresh(String refreshToken) {

        jwtTokenProvider.validateRefreshToken(refreshToken);
        String email = jwtTokenProvider.getUserEmailFromToken(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND, "Refresh Token이 존재하지 않습니다."));

        if (!storedToken.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISMATCH, "Refresh Token이 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(email);

        return new TokenResponseDTO(newAccessToken, refreshToken);
    }
}
