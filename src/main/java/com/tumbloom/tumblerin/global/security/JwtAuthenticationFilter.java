package com.tumbloom.tumblerin.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tumbloom.tumblerin.global.dto.ApiResponseTemplate;
import com.tumbloom.tumblerin.global.dto.ErrorCode;
import com.tumbloom.tumblerin.global.exception.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private boolean isWhiteListed(String path) {
        for (String pattern : SecurityConstants.AUTH_WHITELIST) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (isWhiteListed(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Authorization 헤더 없음 - method: {}, path: {}, IP: {}",
                    request.getMethod(), path, request.getRemoteAddr());
            sendErrorResponse(response, "Authorization 헤더가 없습니다.");
            return;
        }

        String token = authHeader.substring(7);
        try {
            jwtTokenProvider.validateToken(token);
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (BusinessException e) {
            log.warn("JWT 인증 실패 - method: {}, path: {}, IP: {}, reason: {}",
                    request.getMethod(), path, request.getRemoteAddr(), e.getMessage());
            sendErrorResponse(response, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ApiResponseTemplate<String> errorResponse = ApiResponseTemplate
                .error(ErrorCode.UNAUTHORIZED_EXCEPTION, message)
                .getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(json);
    }
}
