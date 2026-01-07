package com.nova.yeobaek.global.auth.dto.response;

public class ResponseDTO {

    public record LoginResponse(
            String accessToken,
            String refreshToken,
            boolean isNewUser
    ) {}
}
