package com.nova.yeobaek.global.auth.oauth.service;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.enums.OauthProvider;
import com.nova.yeobaek.domain.user.domain.enums.Role;
import com.nova.yeobaek.domain.user.repository.UserRepository;
import com.nova.yeobaek.global.auth.oauth.exception.OAuthException;
import com.nova.yeobaek.global.auth.oauth.exception.code.OAuthErrorStatus;
import com.nova.yeobaek.global.auth.oauth.user.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * OAuth Provider(Kakao)에서 사용자 정보를 받아
     * 우리 서비스의 User 엔티티로 변환하는 진입 지점
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        // OAuth Provider에 실제로 사용자 정보 요청
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // application.yml에 등록한 registrationId (kakao)
        String registrationId =
                userRequest.getClientRegistration().getRegistrationId();


        if ("kakao".equals(registrationId)) {
            return processKakaoUser(oAuth2User);
        }

        // 지원하지 않는 OAuth Provider
        throw new OAuthException(OAuthErrorStatus.NOT_SUPPORTED_OAUTH);
    }

    // Kakao OAuth 처리    Kakao 응답은 depth가 깊기 때문에 별도 파싱 필요
    @SuppressWarnings("unchecked")
    private OAuth2User processKakaoUser(OAuth2User oAuth2User) {

        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Kakao 고유 식별자 (필수)
        Object idObj = attributes.get("id");
        if (idObj == null) {
            throw new OAuthException(OAuthErrorStatus.OAUTH_RESPONSE_MISSING_ID);
        }

        String socialId = idObj.toString();

        // kakao_account → profile → profile_image_url
        Map<String, Object> kakaoAccount =
                (Map<String, Object>) attributes.get("kakao_account");

        Map<String, Object> profile =
                kakaoAccount != null
                        ? (Map<String, Object>) kakaoAccount.get("profile")
                        : null;

        User user = getOrCreateSocialUser(
                OauthProvider.KAKAO,
                socialId
        );

        return new CustomOAuth2User(user, attributes);
    }

    //  소셜 타입 + 소셜 ID 기준으로 사용자 조회, 없으면 신규 생성
    private User getOrCreateSocialUser(
            OauthProvider oauthProvider,
            String socialId) {
        return userRepository
                .findByOauthProviderAndOauthId(oauthProvider, socialId)
                .orElseGet(() ->
                        userRepository.save(
                                User.createSocialUser(
                                        oauthProvider,
                                        socialId,
                                        getRole()
                                )
                        )
                );
    }

    private Role getRole() {
        return Role.USER;
    }
}
