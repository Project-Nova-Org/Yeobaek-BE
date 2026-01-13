package com.nova.yeobaek.domain.ootd.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OOTDRequestDTO {

    @NotBlank(message = "OOTD 이름은 필수입니다.")
    private String name;

    @NotNull(message = "TPO ID는 필수입니다.")
    private Long tpoId;

    @NotNull(message = "Style ID는 필수입니다.")
    private Long styleId;

    private String memo;

    @NotBlank(message = "배경색은 필수입니다.")
    private String imageBackground;

    @NotEmpty(message = "아이템은 하나 이상 필요합니다.")
    @Valid
    private List<OOTDItemRequestDTO> items;

    @Getter
    @NoArgsConstructor
    public static class OOTDItemRequestDTO {

        @NotNull(message = "아이템 ID는 필수입니다.")
        private Long fashionItemId;

        @NotNull(message = "posX는 필수입니다.")
        private Float posX;

        @NotNull(message = "posY는 필수입니다.")
        private Float posY;

        @NotNull(message = "scale은 필수입니다.")
        private Float scale;

        @NotNull(message = "rotation은 필수입니다.")
        private Float rotation;

        @NotNull(message = "zIndex는 필수입니다.")
        @JsonProperty("zIndex")
        private Long zIndex;
    }
}