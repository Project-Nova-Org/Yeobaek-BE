package com.nova.yeobaek.global.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RequestDTO {

    public record SignupRequest(
        @NotBlank
        @Size(min = 1, max = 10)
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣]+$",
                message = "닉네임에는 한글, 영문, 숫자만 사용할 수 있습니다."
        )
        String newNickname
    ){}

    public record SocialLoginRequest(
            @NotBlank String provider, // google, kakao
            @NotBlank String token
    ){}

    public record ReissueRequest(
            @NotBlank String refreshToken
    ) {}
}
