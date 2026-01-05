package com.nova.yeobaek.global.auth.token;

public interface RefreshTokenStore {

    // refresh token 저장 (로그인, 재발급 시)
    void save(Long userId, String refreshToken);

    // refresh token 조회 (재발급 검증 시)
    String get(Long userId);

    // refresh token 삭제 (로그아웃 시)
    void delete(Long userId);
}

