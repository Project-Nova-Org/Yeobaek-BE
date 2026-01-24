package com.nova.yeobaek.domain.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public class ItemResponseDTO {

    @Schema(description = "아이템 생성 응답")
    public record CreateResponse(
            @Schema(description = "생성된 아이템 ID", example = "1")
            Long itemId
    ) {
    }
}
