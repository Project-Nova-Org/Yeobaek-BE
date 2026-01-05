package com.nova.yeobaek.global.auth.security;

import com.nova.yeobaek.domain.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/*
 * JWT 인증 후 API 요청을 처리하기 위한 표준 Security Principal
 * Spring Security의 기본 인증 모델
 * 매 요청마다 필요하다
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;
    private static final String DEFAULT_ROLE = "ROLE_USER";

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 모든 인증된 사용자는 USER 권한
        return List.of(new SimpleGrantedAuthority(DEFAULT_ROLE));
    }

    // 인증식별자
    @Override
    public String getUsername() {
        return user.getId().toString();
    }

    @Override
    public String getPassword() {
        return null; // 소셜 로그인
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
