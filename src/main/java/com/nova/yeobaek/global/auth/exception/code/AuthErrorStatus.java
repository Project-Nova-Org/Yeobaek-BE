package com.nova.yeobaek.global.auth.exception.code;

import com.nova.yeobaek.global.payload.status.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorStatus implements ErrorReason {

    NOT_FOUND(HttpStatus.NOT_FOUND,"AUTH_4042","사용자를 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"AUTH_4043","리프레시토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.CONFLICT,"AUTH_4091","저장된 리프레시토큰과 다릅니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST,"AUTH_4003", "유효하지 않은 리프레시토큰입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
