package com.nova.yeobaek.domain.ootd.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OOTDRequestDTO {

    /** OOTD 생성 요청 */
    public record Create(
            @NotBlank(message = "OOTD 이름은 필수입니다.")
            String name,

            @NotNull(message = "TPO ID는 필수입니다.")
            Long tpoId,

            @NotNull(message = "Style ID는 필수입니다.")
            Long styleId,

            String memo,

            @NotBlank(message = "배경색은 필수입니다.")
            String imageBackground,

            @NotEmpty(message = "아이템은 하나 이상 필요합니다.")
            @Valid
            List<Item> items
    ) {}

    /** OOTD 아이템 요청 */
    public record Item(
            @NotNull(message = "아이템 ID는 필수입니다.")
            Long fashionItemId,

            @NotNull(message = "posX는 필수입니다.")
            Float posX,

            @NotNull(message = "posY는 필수입니다.")
            Float posY,

            @NotNull(message = "scale은 필수입니다.")
            Float scale,

            @NotNull(message = "rotation은 필수입니다.")
            Float rotation,

            @NotNull(message = "zIndex는 필수입니다.")
            @JsonProperty("zIndex")
            Long zIndex
    ) {}
}