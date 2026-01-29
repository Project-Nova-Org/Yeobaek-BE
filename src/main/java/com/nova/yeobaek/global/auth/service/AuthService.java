package com.nova.yeobaek.global.auth.service;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.enums.OauthProvider;
import com.nova.yeobaek.domain.user.domain.enums.Role;
import com.nova.yeobaek.domain.user.domain.enums.UserStatus;
import com.nova.yeobaek.domain.user.repository.UserRepository;
import com.nova.yeobaek.global.auth.dto.google.GoogleUserResponse;
import com.nova.yeobaek.global.auth.dto.kakao.KakaoUserResponse;
import com.nova.yeobaek.global.auth.dto.request.RequestDTO;
import com.nova.yeobaek.global.auth.dto.response.ResponseDTO;
import com.nova.yeobaek.global.auth.exception.AuthException;
import com.nova.yeobaek.global.auth.exception.code.AuthErrorStatus;
import com.nova.yeobaek.global.auth.jwt.JwtTokenProvider;
import com.nova.yeobaek.global.auth.token.AccessTokenBlacklistStore;
import com.nova.yeobaek.global.auth.token.RefreshTokenStore;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.nova.yeobaek.domain.user.domain.User.createSocialUser;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final UserRepository userRepository;
    private final AccessTokenBlacklistStore accessTokenBlacklistStore;

    // 소셜 로그인
    public ResponseDTO.LoginResponse socialLogin(RequestDTO.SocialLoginRequest request) {

        OauthProvider provider = OauthProvider.from(request.provider());

        String oauthId = switch (provider) {
            case KAKAO -> verifyKakaoTokenAndGetId(request.token());
            case GOOGLE -> verifyGoogleTokenAndGetId(request.token());
        };


        User user = userRepository
                .findByOauthProviderAndOauthId(provider, oauthId)
                .orElseGet(() -> userRepository.save(
                        createSocialUser(provider, oauthId, Role.USER)
                ));
        if (user.getStatus() == UserStatus.DELETED) {
            throw new AuthException(AuthErrorStatus.WITHDRAWN_USER);
        }

        boolean isNewUser = user.getNickname() == null;

        // JWT 발급
        return issueTokens(user, isNewUser);
    }

    // 개발용 로그인 (local/dev 전용)
    public ResponseDTO.LoginResponse devLogin(
            OauthProvider provider,
            String oauthId
    ) {
        User user = userRepository
                .findByOauthProviderAndOauthId(provider, oauthId)
                .orElseGet(() ->
                        userRepository.save(
                                User.createSocialUser(provider, oauthId, Role.USER)
                        )
                );

        boolean isNewUser = user.getNickname() == null;
        return issueTokens(user, isNewUser);
    }


    // 로그아웃
    public void logout(String accessToken, Long userId) {

        if (accessToken != null) {
            try {
                jwtTokenProvider.validateToken(accessToken);

                long ttl = jwtTokenProvider.getRemainingExpirationMillis(accessToken);
                accessTokenBlacklistStore.blacklist(accessToken, ttl);

            } catch (ExpiredJwtException e) {
                log.debug("이미 만료된 토큰");

            } catch (JwtException | IllegalArgumentException e) {
                log.debug("로그아웃 중 유효하지 않은 토큰 무시");
            }
        }
        // Refresh Token 삭제
        refreshTokenStore.delete(userId);
    }

    // accessToken 재발급 후 refreshToken도 재발급
    public ResponseDTO.LoginResponse reissue(String refreshToken) {

        if (refreshToken == null) {
            throw new AuthException(AuthErrorStatus.REFRESH_TOKEN_NOT_FOUND);
        }

        try {
            jwtTokenProvider.validateToken(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(AuthErrorStatus.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);

        String savedRefreshToken = refreshTokenStore.get(userId);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new AuthException(AuthErrorStatus.REFRESH_TOKEN_MISMATCH);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorStatus.USER_NOT_FOUND));

        refreshTokenStore.delete(userId);

        return issueTokens(user, false);
    }

    // 토큰 발급
    private ResponseDTO.LoginResponse issueTokens(User user, boolean isNewUser) {

        Long userId = user.getId();

        String accessToken =
                jwtTokenProvider.createAccessToken(userId, user.getRole().name());
        String refreshToken =
                jwtTokenProvider.createRefreshToken(userId);

        refreshTokenStore.save(userId, refreshToken);

        return new ResponseDTO.LoginResponse(accessToken, refreshToken, isNewUser);
    }

    private String verifyKakaoTokenAndGetId(String token) {

        try {
            KakaoUserResponse response = WebClient.create("https://kapi.kakao.com")
                    .get()
                    .uri("/v2/user/me")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> Mono.error(
                                    new AuthException(AuthErrorStatus.INVALID_OAUTH_TOKEN)
                            )
                    )
                    .bodyToMono(KakaoUserResponse.class)
                    .block();

            if (response == null || response.id() == null) {
                throw new AuthException(AuthErrorStatus.INVALID_OAUTH_TOKEN);
            }

            // oauthId는 String으로 통일
            return response.id().toString();

        } catch (Exception e) {
            throw new AuthException(AuthErrorStatus.INVALID_OAUTH_TOKEN);
        }
    }

    private String verifyGoogleTokenAndGetId(String idToken) {

        try {
            GoogleUserResponse response = WebClient.create(
                            "https://oauth2.googleapis.com"
                    )
                    .get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path("/tokeninfo")
                                    .queryParam("id_token", idToken)
                                    .build()
                    )
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> Mono.error(
                                    new AuthException(AuthErrorStatus.INVALID_OAUTH_TOKEN)
                            )
                    )
                    .bodyToMono(GoogleUserResponse.class)
                    .block();

            if (response == null || response.sub() == null) {
                throw new AuthException(AuthErrorStatus.INVALID_OAUTH_TOKEN);
            }

            return response.sub();

        } catch (Exception e) {
            throw new AuthException(AuthErrorStatus.INVALID_OAUTH_TOKEN);
        }
    }

    public void logoutAll(Long userId) {
        refreshTokenStore.delete(userId);
    }
}
