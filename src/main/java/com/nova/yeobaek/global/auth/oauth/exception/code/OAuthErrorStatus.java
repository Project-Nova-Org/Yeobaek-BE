package com.nova.yeobaek.global.auth.oauth.exception.code;

import com.nova.yeobaek.global.payload.status.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OAuthErrorStatus implements ErrorReason {

    OAUTH_RESPONSE_MAPPING_FAILED(HttpStatus.UNAUTHORIZED,
            "OAUTH4011",
            "OAuth 인증 응답을 사용자 정보로 변환하는 데 실패했습니다."),
    OAUTH_RESPONSE_MISSING_ID(HttpStatus.UNAUTHORIZED,
            "OAUTH4012",
            "OAuth 인증 응답에 필수 식별자(id)가 포함되어 있지 않습니다."),
    NOT_SUPPORTED_OAUTH(HttpStatus.BAD_REQUEST,
            "OAUTH4001",
            "지원하지않는 방식의 소셜로그인입니다."),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
