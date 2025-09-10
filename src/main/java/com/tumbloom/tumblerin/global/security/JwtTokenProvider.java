package com.tumbloom.tumblerin.global.security;

import com.tumbloom.tumblerin.app.security.CustomUserDetailsService;
import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    // access token 유효기간: 60분으로 수정
    private final long accessTokenValidTime = 1000L * 60 * 3;
    // refresh token 발급: 15일 정도
    private final long refreshTokenValidTime = 1000L * 60 * 3; //60 * 24 * 15;



    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createAccessToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidTime);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidTime);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        String email = getUserEmailFromToken(token);
        var userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    //accesstoken 검증
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | io.jsonwebtoken.MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.", e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "잘못된 JWT 토큰입니다.");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.", e);
            throw new BusinessException(ErrorCode.ACCESS_TOKEN_EXPIRED, "ACCESS_TOKEN_EXPIRED");
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.info("지원하지 않는 JWT 토큰입니다.", e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "지원하지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 비어있습니다.", e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "JWT 토큰이 비어있습니다.");
        }
    }

    public void validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | io.jsonwebtoken.MalformedJwtException e) {
            log.info("잘못된 Refresh JWT 서명입니다.", e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "잘못된 Refresh JWT 토큰입니다.");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.info("만료된 Refresh JWT 토큰입니다.", e);
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED, "REFRESH_TOKEN_EXPIRED");
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.info("지원하지 않는 Refresh JWT 토큰입니다.", e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "지원하지 않는 Refresh JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("Refresh JWT 토큰이 비어있습니다.", e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "Refresh JWT 토큰이 비어있습니다.");
        }
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)// 프론트 배포시 HTTPS 환경에서만 전송
                .path("/")
                .maxAge(refreshTokenValidTime / 1000)
                .sameSite("Strict")          // CSRF 방어
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void removeRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) //즉시 만료
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

}
