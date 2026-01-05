package com.nova.yeobaek.global.auth.oauth.service;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.enums.OauthProvider;
import com.nova.yeobaek.domain.user.domain.enums.Role;
import com.nova.yeobaek.domain.user.repository.UserRepository;
import com.nova.yeobaek.global.auth.oauth.exception.OAuthException;
import com.nova.yeobaek.global.auth.oauth.exception.code.OAuthErrorStatus;
import com.nova.yeobaek.global.auth.oauth.user.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {

        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> attributes = oidcUser.getAttributes();

        // Google 고유 ID
        Object subObj = attributes.get("sub");
        if (subObj == null) {
            throw new OAuthException(OAuthErrorStatus.OAUTH_RESPONSE_MISSING_ID);
        }
        String socialId = subObj.toString();

        User user = userRepository
                .findByOauthProviderAndOauthId(OauthProvider.GOOGLE, socialId)
                .orElseGet(() ->
                        userRepository.save(
                                User.createSocialUser(
                                        OauthProvider.GOOGLE,
                                        socialId,
                                        getRole()
                                )
                        )
                );

        return new CustomOAuth2User(user, attributes);
    }

    private Role getRole() {
        return Role.USER;
    }
}
