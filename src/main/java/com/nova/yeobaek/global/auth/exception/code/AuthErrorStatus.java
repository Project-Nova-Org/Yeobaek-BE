package com.nova.yeobaek.global.auth.exception.code;

import com.nova.yeobaek.global.payload.status.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorStatus implements ErrorReason {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"AUTH_4042","사용자를 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"AUTH_4043","리프레시토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.CONFLICT,"AUTH_4091","저장된 리프레시토큰과 다릅니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST,"AUTH_4003", "유효하지 않은 액세스토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST,"AUTH_4004", "유효하지 않은 리프레시토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_4012", "토큰이 만료되었습니다."),
    BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_4013", "이미 로그아웃된 토큰입니다."),
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST,"AUTH_4005","유효하지 않은 소셜 로그인 타입입니다."),
    INVALID_OAUTH_TOKEN(HttpStatus.BAD_REQUEST,"AUTH_4006","유효하지 않은 소셜 로그인 토큰입니다."),
    WITHDRAWN_USER(HttpStatus.BAD_REQUEST,"AUTH_4007","탈퇴한 유저입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
