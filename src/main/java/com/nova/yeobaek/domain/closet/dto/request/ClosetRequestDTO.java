package com.nova.yeobaek.domain.closet.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ClosetRequestDTO {

    @Schema(name = "ClosetCreateRequest")
    public record Create(
            @NotBlank(message = "name은 필수입니다.")
            @Pattern(
                    regexp = "^[a-zA-Z0-9가-힣\\s]+$",
                    message = "name은 영문/숫자/한글/공백만 허용됩니다."
            )
            String name,

            String imageUrl,

            @Valid
            @NotEmpty(message = "items는 1개 이상 필수입니다.")
            List<ItemIdOnly> items
    ) {}

    @Schema(name = "ClosetCreateItem")
    public record ItemIdOnly(
            @NotNull(message = "itemId는 필수입니다.")
            Long itemId
    ) {}

    public record FavoriteUpdate(
            @NotNull(message = "favorite는 필수입니다.")
            Boolean favorite
    ) {}
}
