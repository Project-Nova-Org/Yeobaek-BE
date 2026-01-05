package com.nova.yeobaek.global.auth.oauth.user;

import com.nova.yeobaek.domain.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/*
 * OAuth / OIDC 공통 Principal
 *
 * - Kakao (OAuth2User)
 * - Google (OidcUser)
 *
 * 역할:
 * 1. Spring Security Principal
 * 2. 도메인 User 래핑
 * 3. OAuth attributes 보존
 */
@Getter
public class CustomOAuth2User implements OidcUser {

    private final User user;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    /* ================= Spring Security ================= */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 필요 시 Role 기반으로 변경 가능
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getName() {
        // Principal 식별자 (보통 userId)
        return user.getId().toString();
    }

    /* ================= OAuth2User ================= */

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /* ================= OidcUser ================= */

    @Override
    public Map<String, Object> getClaims() {
        // OIDC claims == attributes
        return attributes;
    }

    @Override
    public Object getClaim(String claim) {
        return attributes.get(claim);
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

    /*
     * 아래 메서드들은 Google에서만 의미 있음
     */

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getFullName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getGivenName() {
        return (String) attributes.get("given_name");
    }

    @Override
    public String getFamilyName() {
        return (String) attributes.get("family_name");
    }

    @Override
    public String getPicture() {
        return (String) attributes.get("picture");
    }

    @Override
    public String getLocale() {
        return (String) attributes.get("locale");
    }
}
