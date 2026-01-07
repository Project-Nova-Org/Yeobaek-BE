package com.nova.yeobaek.global.auth.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserResponse(
        @JsonProperty("sub") String sub
) {}
