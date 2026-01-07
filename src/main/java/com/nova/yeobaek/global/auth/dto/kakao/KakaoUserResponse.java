package com.nova.yeobaek.global.auth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserResponse(
        @JsonProperty("id") Long id
) {}
