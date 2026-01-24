package com.nova.yeobaek.domain.item.controller.docs;

import com.nova.yeobaek.domain.item.dto.request.ItemRequestDTO;
import com.nova.yeobaek.domain.item.dto.response.ItemResponseDTO;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Item", description = "아이템 API")
public interface ItemControllerDocs {

    @Operation(
            summary = "아이템 등록",
            description = """
            새로운 의류 아이템을 등록합니다.

            **필수 항목:**
            - imageUrl: 이미지 URL
            - imageBackground: 배경 색상 (WHITE, GRAY)
            - categoryId: 카테고리 ID (2레벨)
            - colors: 색상 목록 (1~2개)
              - BLACK, WHITE, GRAY, BEIGE, BROWN, RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE, PINK
            - seasons: 계절 목록 (1개 이상)
              - SPRING, SUMMER, AUTUMN, WINTER

            **선택 항목:**
            - brandName: 브랜드명 (없으면 자동 생성)
            - material: 소재
              - 데님, 면, 나일론, 폴리에스터, 가죽, 캐시미어, 울, 스웨이드, 코듀로이, 아크릴, 레이온, 린넨, 알파카, 기타
            - size: 사이즈 (자유 입력)
            - price: 가격
            - memo: 메모
            """
    )
    @ApiResponse(
            responseCode = "201",
            description = "아이템 등록 성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "INVALID_COLOR",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "ITEM4003",
                                      "message": "유효하지 않은 색상입니다.",
                                      "timestamp": "2026-01-24T15:00:00"
                                    }
                                    """
                            ),
                            @ExampleObject(
                                    name = "INVALID_SEASON",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "ITEM4005",
                                      "message": "유효하지 않은 계절입니다.",
                                      "timestamp": "2026-01-24T15:00:00"
                                    }
                                    """
                            ),
                            @ExampleObject(
                                    name = "INVALID_MATERIAL",
                                    value = """
                                    {
                                      "success": false,
                                      "code": "ITEM4006",
                                      "message": "유효하지 않은 소재입니다.",
                                      "timestamp": "2026-01-24T15:00:00"
                                    }
                                    """
                            )
                    }
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "카테고리를 찾을 수 없음",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "CATEGORY_NOT_FOUND",
                            value = """
                            {
                              "success": false,
                              "code": "ITEM4041",
                              "message": "존재하지 않는 카테고리입니다.",
                              "timestamp": "2026-01-24T15:00:00"
                            }
                            """
                    )
            )
    )
    CommonResponse<ItemResponseDTO.CreateResponse> createItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ItemRequestDTO.Create request
    );
}
