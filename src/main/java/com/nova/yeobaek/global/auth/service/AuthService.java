package com.nova.yeobaek.global.auth.service;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.auth.exception.AuthException;
import com.nova.yeobaek.global.auth.exception.code.AuthErrorStatus;
import com.nova.yeobaek.global.auth.jwt.JwtTokenProvider;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.auth.token.RefreshTokenStore;
import com.nova.yeobaek.global.auth.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final CookieUtil cookieUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;

    public void login(
            User user,
            HttpServletResponse response
    ) {
        // 사용자 식별자
        Long userId = user.getId();

        // Access / Refresh 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(userId, "USER");
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        /*
           RefreshToken 저장
          - Redis 기준
          - key: refresh:{userId}
          - TTL = refresh token 만료 시간
         */
        refreshTokenStore.save(userId, refreshToken);

        /*
           쿠키 설정

          - accessToken  : 짧은 만료, HttpOnly
          - refreshToken : 긴 만료, HttpOnly
          - local / dev / prod 환경에 따라 옵션 자동 분기
         */
        response.addHeader(
                "Set-Cookie",
                cookieUtil.accessToken(accessToken).toString()
        );
        response.addHeader(
                "Set-Cookie",
                cookieUtil.refreshToken(refreshToken).toString()
        );
    }

    public void logout(
            HttpServletResponse response
    ){
        log.debug("로그아웃 요청.");

        // 🔥 SecurityContext 상태와 무관하게 항상 쿠키 삭제
        response.addHeader(
                "Set-Cookie",
                cookieUtil.delete("accessToken").toString()
        );
        response.addHeader(
                "Set-Cookie",
                cookieUtil.delete("refreshToken").toString()
        );

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String)) {

            CustomUserDetails userDetails =
                    (CustomUserDetails) authentication.getPrincipal();

            refreshTokenStore.delete(userDetails.getUser().getId());
        }

        SecurityContextHolder.clearContext();
        log.debug("로그아웃 성공.");
    }

    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        // Refresh Token 추출 (쿠키)
        String refreshToken = cookieUtil.resolveRefreshToken(request);

        if (refreshToken == null) {
            throw new AuthException(AuthErrorStatus.REFRESH_TOKEN_NOT_FOUND);
        }

        // Refresh Token 유효성 검증 (서명 + 만료)
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthException(AuthErrorStatus.INVALID_REFRESH_TOKEN);
        }

        // Refresh Token에서 userId 추출
        Long userId = jwtTokenProvider.getUserId(refreshToken);

        // Redis에 저장된 Refresh Token과 비교
        String savedRefreshToken = refreshTokenStore.get(userId);

        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new AuthException(AuthErrorStatus.REFRESH_TOKEN_MISMATCH);
        }

        // 새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(userId, "USER");

        // Access Token 쿠키 재설정
        response.addHeader(
                "Set-Cookie",
                cookieUtil.accessToken(newAccessToken).toString()
        );
    }
}
