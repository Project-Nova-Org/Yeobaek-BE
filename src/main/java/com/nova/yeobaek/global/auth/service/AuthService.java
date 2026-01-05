package com.nova.yeobaek.global.auth.service;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.repository.UserRepository;
import com.nova.yeobaek.global.auth.exception.AuthException;
import com.nova.yeobaek.global.auth.exception.code.AuthErrorStatus;
import com.nova.yeobaek.global.auth.jwt.JwtTokenProvider;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.auth.token.AccessTokenBlacklistStore;
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
    private final UserRepository userRepository;
    private final AccessTokenBlacklistStore accessTokenBlacklistStore;

    // 로그인
    public void login(User user, HttpServletResponse response) {
        issueTokens(user, response);
    }

    // 로그아웃
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        // Access Token 추출
        String accessToken = cookieUtil.resolveAccessToken(request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            long ttl =
                    jwtTokenProvider.getRemainingExpirationMillis(accessToken);

            // 블랙리스트 등록
            accessTokenBlacklistStore.blacklist(accessToken, ttl);
        }

        // 쿠키 삭제
        response.addHeader(
                "Set-Cookie",
                cookieUtil.delete("accessToken").toString()
        );
        response.addHeader(
                "Set-Cookie",
                cookieUtil.delete("refreshToken").toString()
        );

        // Refresh Token 삭제
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
                authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

            refreshTokenStore.delete(userDetails.getUser().getId());
        }

        SecurityContextHolder.clearContext();
    }


    // accessToken 재발급 후 refreshToken도 재발급
    public void reissue(HttpServletRequest request, HttpServletResponse response) {

        // Refresh Token 추출
        String refreshToken = cookieUtil.resolveRefreshToken(request);
        if (refreshToken == null) {
            throw new AuthException(AuthErrorStatus.REFRESH_TOKEN_NOT_FOUND);
        }

        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthException(AuthErrorStatus.INVALID_REFRESH_TOKEN);
        }

        // userId 추출
        Long userId = jwtTokenProvider.getUserId(refreshToken);

        // Redis Refresh Token 비교
        String savedRefreshToken = refreshTokenStore.get(userId);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new AuthException(AuthErrorStatus.REFRESH_TOKEN_MISMATCH);
        }

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorStatus.NOT_FOUND));

        // 기존 Refresh Token 폐기
        refreshTokenStore.delete(userId);

        // 새 토큰 발급
        issueTokens(user, response);
    }

    // 토큰 발급
    private void issueTokens(User user, HttpServletResponse response) {

        Long userId = user.getId();

        // Access / Refresh Token 생성
        String accessToken =
                jwtTokenProvider.createAccessToken(userId, user.getRole().name());
        String refreshToken =
                jwtTokenProvider.createRefreshToken(userId);

        // Refresh Token 저장 (Redis)
        refreshTokenStore.save(userId, refreshToken);

        // Access Token 쿠키
        response.addHeader(
                "Set-Cookie",
                cookieUtil.accessToken(accessToken).toString()
        );

        // Refresh Token 쿠키
        response.addHeader(
                "Set-Cookie",
                cookieUtil.refreshToken(refreshToken).toString()
        );
    }
}
