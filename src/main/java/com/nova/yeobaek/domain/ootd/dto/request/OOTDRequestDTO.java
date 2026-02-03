package com.nova.yeobaek.domain.ootd.dto.request;

import static com.nova.yeobaek.domain.item.dto.request.ItemRequestDTO.ItemSearchCondition.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

public class OOTDRequestDTO {

    /** OOTD 생성 요청 */
    public record CreateOOTD(
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
    ) {    }

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
    ) {    }

    /** OOTD 목록 조회 조건 */
    public record OOTDSearchCondition(
            @Schema(description = "검색 키워드")
            String keyword,

            @Schema(description = "즐겨찾기 여부")
            Boolean favorite,

            @Schema(description = "TPO ID 목록")
            List<Long> tpoId,

            @Schema(description = "Style ID 목록")
            List<Long> styleId,

            @Schema(
                    description = "정렬 기준: LATEST, NAME_ASC",
                    defaultValue = "LATEST"
            )
            SortType sort,

            @Schema(description = "커서 ID (이전 페이지 마지막 ID)")
            Long cursor,

            @Schema(description = "커서 이름 (이름순 정렬 시 필수)")
            String cursorName,

            @Schema(
                    description = "조회 개수 (기본 20)",
                    defaultValue = "20"
            )
            @Min(1)
            @Max(100)
            Integer limit
    ) {

        public SortType resolvedSort() {
            return sort != null ? sort : SortType.LATEST;
        }

        public int resolvedLimit() {
            return limit != null ? limit : 20;
        }

        public List<Long> resolvedTpoIds() {
            return (tpoId == null || tpoId.isEmpty()) ? null : tpoId;
        }

        public List<Long> resolvedStyleIds() {
            return (styleId == null || styleId.isEmpty()) ? null : styleId;
        }
    }

    /** OOTD 수정 요청 */
    public record UpdateOOTD(

            @Schema(description = "OOTD 이름")
            String name,

            @Schema(description = "OOTD 메모")
            String memo,

            @Schema(description = "즐겨찾기 여부")
            Boolean favorite,

            @Schema(description = "TPO ID")
            Long tpoId,

            @Schema(description = "Style ID")
            Long styleId,

            @Schema(description = "이미지 배경 색상")
            String imageBackground,

            @Schema(description = "OOTD 아이템 목록 (전체 교체)")
            @Valid
            List<Item> items
    ) {
    }
}