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
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

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
    private final long accessTokenValidTime = 1000L * 60 * 60;
    // refresh token 발급: 30일
    private final long refreshTokenValidTime = 1000L * 60 * 60 * 24 * 30;


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
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "만료된 JWT 토큰입니다.");
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
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "만료된 Refresh JWT 토큰입니다.");
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.info("지원하지 않는 Refresh JWT 토큰입니다.", e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "지원하지 않는 Refresh JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("Refresh JWT 토큰이 비어있습니다.", e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "Refresh JWT 토큰이 비어있습니다.");
        }
    }

}
