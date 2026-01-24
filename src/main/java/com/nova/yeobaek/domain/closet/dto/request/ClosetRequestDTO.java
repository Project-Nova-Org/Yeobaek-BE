package com.nova.yeobaek.domain.closet.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClosetRequestDTO {

    public record Create(
            @NotBlank String name,
            String imageUrl,
            List<ItemPlacement> items
    ) {}

    public record ItemPlacement(
            // 내부 검증은 컨트롤러/서비스에서 할 수도 있지만,
            // 최소로 유지하려면 일단 NotNull 제거해도 되고,
            // 아래처럼 두고 컨트롤러에서 items 미포함만 잡아도 됨.
            @NotNull(message = "itemId는 필수입니다.")
            Long itemId,

            @NotNull(message = "posX는 필수입니다.")
            Double posX,

            @NotNull(message = "posY는 필수입니다.")
            Double posY,

            @NotNull(message = "scale은 필수입니다.")
            Double scale,

            @NotNull(message = "rotation은 필수입니다.")
            Double rotation,

            @NotNull(message = "zIndex는 필수입니다.")
            Integer zIndex
    ) {}
}
