package com.nova.yeobaek.domain.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import com.nova.yeobaek.domain.item.domain.enums.Season;

public class ItemRequestDTO {

    /** 아이템 목록 조회 조건 */
    public record ItemSearchCondition(
            @Schema(description = "카테고리 ID (2레벨)")
            Long categoryId,

            @Schema(description = "계절 (SPRING, SUMMER, AUTUMN, WINTER)")
            Season season,

            @Schema(description = "소재")
            String material,

            @Schema(description = "검색 키워드 (브랜드명, 메모)")
            String keyword,

            @Schema(description = "정렬 기준: LATEST, NAME_ASC", defaultValue = "LATEST")
            SortType sort,

            @Schema(description = "커서 ID (이전 페이지 마지막 ID)")
            Long cursor,

            @Schema(description = "커서 브랜드명 (이름순 정렬 시 필수)")
            String cursorBrandName,

            @Schema(description = "조회 개수 (기본 20)", defaultValue = "20")
            @Min(1)
            @Max(100)
            Integer limit
    ) {
        public enum SortType {
            LATEST, NAME_ASC
        }

        public SortType resolvedSort() {
            return sort != null ? sort : SortType.LATEST;
        }

        public int resolvedLimit() {
            return limit != null ? limit : 20;
        }
    }

    @Schema(description = "아이템 생성 요청")
    public record CreateItem(
            @Schema(description = "이미지 URL", example = "https://example.com/image.png")
            @NotBlank(message = "이미지 URL은 필수입니다.")
            String imageUrl,

            @Schema(description = "이미지 배경 색상", example = "WHITE", allowableValues = {"WHITE", "GRAY"})
            @NotBlank(message = "이미지 배경 색상은 필수입니다.")
            String imageBackground,

            @Schema(description = "카테고리 ID (2레벨)", example = "5")
            @NotNull(message = "카테고리 ID는 필수입니다.")
            Long categoryId,

            @Schema(description = "색상 목록 (1~2개)", example = "[\"BLACK\", \"WHITE\"]")
            @NotEmpty(message = "색상은 최소 1개 이상 선택해야 합니다.")
            @Size(max = 2, message = "색상은 최대 2개까지 선택할 수 있습니다.")
            List<String> colors,

            @Schema(description = "계절 목록 (1개 이상)", example = "[\"SPRING\", \"SUMMER\"]")
            @NotEmpty(message = "계절은 최소 1개 이상 선택해야 합니다.")
            List<String> seasons,

            @Schema(description = "브랜드명", example = "나이키")
            @NotBlank(message = "브랜드명은 필수입니다.")
            String brandName,

            @Schema(description = "소재", example = "면")
            @NotBlank(message = "소재는 필수입니다.")
            String material,

            @Schema(description = "사이즈", example = "M")
            String size,

            @Schema(description = "가격", example = "50000")
            Long price,

            @Schema(description = "메모", example = "편한 티셔츠")
            String memo
    ) {
    }

    @Schema(description = "아이템 수정 요청 (모든 필드 선택)")
    public record UpdateItem(
            @Schema(description = "이미지 URL", example = "https://example.com/new-image.png")
            String imageUrl,

            @Schema(description = "이미지 배경 색상", example = "GRAY", allowableValues = {"WHITE", "GRAY"})
            String imageBackground,

            @Schema(description = "카테고리 ID (2레벨)", example = "7")
            Long categoryId,

            @Schema(description = "색상 목록 (1~2개)", example = "[\"RED\"]")
            @Size(min = 1, max = 2, message = "색상은 1~2개 선택해야 합니다.")
            List<String> colors,

            @Schema(description = "계절 목록 (1개 이상)", example = "[\"WINTER\"]")
            @Size(min = 1, message = "계절은 최소 1개 이상 선택해야 합니다.")
            List<String> seasons,

            @Schema(description = "브랜드명", example = "아디다스")
            String brandName,

            @Schema(description = "소재", example = "울")
            String material,

            @Schema(description = "사이즈", example = "L")
            String size,

            @Schema(description = "가격", example = "80000")
            Long price,

            @Schema(description = "메모", example = "따뜻한 니트")
            String memo
    ) {
    }
}
