package com.nova.yeobaek.domain.closet.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ClosetRequestDTO {

    public record Create(
            @NotBlank String name,
            String imageUrl
    ) {}

}
