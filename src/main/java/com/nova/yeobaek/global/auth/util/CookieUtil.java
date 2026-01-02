package com.nova.yeobaek.global.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

@Component
public class CookieUtil {

    private final boolean httpOnly;
    private final boolean secure;
    private final String sameSite;

    public CookieUtil(
            @Value("${app.cookie.http-only}") boolean httpOnly,
            @Value("${app.cookie.secure}") boolean secure,
            @Value("${app.cookie.same-site}") String sameSite
    ) {
        this.httpOnly = httpOnly;
        this.secure = secure;
        this.sameSite = sameSite;
    }

    public ResponseCookie accessToken(String token) {
        return ResponseCookie.from("accessToken", token)
                .httpOnly(httpOnly)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .build();
    }


    public ResponseCookie refreshToken(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(httpOnly)
                .secure(secure)
                .sameSite(sameSite)
                .maxAge(7 * 24 * 60 * 60)
                .path("/")
                .build();
    }

    public ResponseCookie delete(String name) {
        return ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .build();
    }

    public String resolveAccessToken(HttpServletRequest request) {
        return resolveToken(request, "accessToken");
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return resolveToken(request, "refreshToken");
    }

    public String resolveToken(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
