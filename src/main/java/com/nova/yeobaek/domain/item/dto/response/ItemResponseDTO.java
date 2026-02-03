package com.nova.yeobaek.domain.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ItemResponseDTO {

    @Schema(description = "아이템 생성 응답")
    public record CreateResponse(
            @Schema(description = "생성된 아이템 ID", example = "1")
            Long itemId
    ) {
    }

    @Schema(description = "아이템 목록 응답")
    public record ListResponse(
            @Schema(description = "아이템 목록")
            List<ListItem> items,

            @Schema(description = "다음 커서 ID")
            Long nextCursor,

            @Schema(description = "다음 커서 이름 (이름순 정렬일 때만 값 있음, 그 외 null)")
            String nextCursorBrandName,

            @Schema(description = "다음 페이지 존재 여부")
            boolean hasNext
    ) {
    }

    @Schema(description = "아이템 목록 항목")
    public record ListItem(
            @Schema(description = "아이템 ID", example = "1")
            Long itemId,

            @Schema(description = "이미지 URL", example = "https://example.com/image.png")
            String imageUrl,

            @Schema(description = "이미지 배경 색상", example = "WHITE")
            String imageBackground,

            @Schema(description = "브랜드명", example = "나이키")
            String brandName
    ) {
    }

    @Schema(description = "아이템 상세 응답")
    public record DetailResponse(
            @Schema(description = "아이템 ID", example = "1")
            Long itemId,

            @Schema(description = "이미지 URL", example = "https://example.com/image.png")
            String imageUrl,

            @Schema(description = "이미지 배경 색상", example = "WHITE")
            String imageBackground,

            @Schema(description = "카테고리 ID", example = "5")
            Long categoryId,

            @Schema(description = "카테고리명", example = "반팔 티셔츠")
            String categoryName,

            @Schema(description = "색상 목록", example = "[\"BLACK\", \"WHITE\"]")
            List<String> colors,

            @Schema(description = "계절 목록", example = "[\"SPRING\", \"SUMMER\"]")
            List<String> seasons,

            @Schema(description = "브랜드명", example = "나이키")
            String brandName,

            @Schema(description = "소재", example = "면")
            String material,

            @Schema(description = "사이즈", example = "M")
            String size,

            @Schema(description = "가격", example = "50000")
            Long price,

            @Schema(description = "메모", example = "편한 티셔츠")
            String memo,

            @Schema(description = "착용 횟수", example = "5")
            int useCount,

            @Schema(description = "최근 착용 날짜", example = "2026-01-15")
            LocalDate lastUsedDate,

            @Schema(description = "생성일시")
            LocalDateTime createdAt
    ) {
    }

    @Schema(description = "아이템이 포함된 OOTD 목록 응답")
    public record ItemOOTDsResponse(
            @Schema(description = "OOTD 목록")
            List<OOTDItem> ootds
    ) {
    }

    @Schema(description = "OOTD 항목")
    public record OOTDItem(
            @Schema(description = "OOTD ID", example = "1")
            Long ootdId,

            @Schema(description = "OOTD 이름", example = "출근룩")
            String name,

            @Schema(description = "OOTD 이미지 URL", example = "https://example.com/ootd.png")
            String imageUrl,

            @Schema(description = "이미지 배경 색상", example = "WHITE")
            String imageBackground
    ) {
    }
}
