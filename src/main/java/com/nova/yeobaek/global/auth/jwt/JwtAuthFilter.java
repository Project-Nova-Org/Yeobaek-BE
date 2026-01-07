package com.nova.yeobaek.global.auth.jwt;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.repository.UserRepository;
import com.nova.yeobaek.global.auth.exception.AuthException;
import com.nova.yeobaek.global.auth.exception.code.AuthErrorStatus;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.auth.token.AccessTokenBlacklistStore;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final AccessTokenBlacklistStore accessTokenBlacklistStore;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return uri.startsWith("/swagger")
                || uri.startsWith("/v3/api-docs")
                || uri.equals("/api/auth/social/login")
                || uri.equals("/api/auth/reissue");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 이미 인증된 경우 그대로 통과
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더에서 accessToken 추출
        String accessToken = resolveAccessToken(request);
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 블랙리스트 체크
        if (accessTokenBlacklistStore.isBlacklisted(accessToken)) {
            throw new AuthException(AuthErrorStatus.BLACKLISTED_TOKEN);
        }

        // 토큰 검증
        try {
            jwtTokenProvider.validateToken(accessToken);
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorStatus.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(AuthErrorStatus.INVALID_ACCESS_TOKEN);
        }

        // 사용자 조회
        Long userId = jwtTokenProvider.getUserId(accessToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorStatus.USER_NOT_FOUND));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}
