package com.nova.yeobaek.domain.item.controller.docs;

import com.nova.yeobaek.domain.item.dto.request.ItemRequestDTO;
import com.nova.yeobaek.domain.item.dto.response.ItemResponseDTO;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

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
            - brandName: 브랜드명
            - material: 소재
              - 데님, 면, 나일론, 폴리에스터, 가죽, 캐시미어, 울, 스웨이드, 코듀로이, 아크릴, 레이온, 린넨, 알파카, 기타

            **선택 항목:**
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
            @RequestBody(
                    description = "아이템 생성 요청",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ItemRequestDTO.CreateItem.class),
                            examples = @ExampleObject(
                                    name = "아이템 생성 예시",
                                    value = """
                                    {
                                      "imageUrl": "https://example.com/image.png",
                                      "imageBackground": "WHITE",
                                      "categoryId": 5,
                                      "colors": ["BLACK", "WHITE"],
                                      "seasons": ["SPRING", "SUMMER"],
                                      "brandName": "나이키",
                                      "material": "면",
                                      "size": "M",
                                      "price": 50000,
                                      "memo": "편한 티셔츠"
                                    }
                                    """
                            )
                    )
            )
            @Valid ItemRequestDTO.CreateItem request
    );

    @Operation(
            summary = "아이템 목록 조회",
            description = """
            로그인 사용자의 아이템 목록을 조회합니다.
            cursor 기반 무한 스크롤을 지원합니다.

            **필터:**
            - categoryId: 카테고리 ID로 필터링
            - season: 계절로 필터링 (SPRING, SUMMER, AUTUMN, WINTER)
            - material: 소재명으로 필터링

            **검색:**
            - keyword: 브랜드명, 메모에서 검색

            **정렬:**
            - sort: LATEST (최신순, 기본값), NAME_ASC (가나다순)

            **페이징:**
            - cursor: 이전 페이지의 마지막 아이템 ID
            - limit: 조회 개수 (기본 20, 최대 100)
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "아이템 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ItemResponseDTO.ListResponse.class))
    )
    CommonResponse<ItemResponseDTO.ListResponse> getItemList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(
                    description = "아이템 목록 조회 조건 (기본값: sort=LATEST, limit=20)",
                    schema = @Schema(
                            defaultValue = "{\"sort\":\"LATEST\",\"limit\":20}"
                    )
            )
            @ParameterObject
            @Valid
            @ModelAttribute
            ItemRequestDTO.ItemSearchCondition condition
    );

    @Operation(
            summary = "아이템 상세 조회",
            description = """
            아이템의 상세 정보를 조회합니다.
            본인 아이템만 조회할 수 있습니다.

            **응답에 포함되는 착용 정보:**
            - useCount: 착용 횟수 (착용 기록이 없으면 0)
            - lastUsedDate: 최근 착용 날짜 (착용 기록이 없으면 null)
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "아이템 상세 조회 성공",
            content = @Content(schema = @Schema(implementation = ItemResponseDTO.DetailResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "아이템을 찾을 수 없음",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "ITEM_NOT_FOUND",
                            value = """
                            {
                              "success": false,
                              "code": "ITEM4042",
                              "message": "존재하지 않거나 접근할 수 없는 아이템입니다.",
                              "timestamp": "2026-01-24T15:00:00"
                            }
                            """
                    )
            )
    )
    CommonResponse<ItemResponseDTO.DetailResponse> getItemDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId
    );

    @Operation(
            summary = "아이템 수정",
            description = """
            아이템 정보를 수정합니다.

            PATCH 방식으로 부분 수정이 가능하며, 전달된 필드만 수정됩니다.
            전달되지 않은 필드는 기존 값을 유지합니다.

            - colors가 전달될 경우 기존 색상을 모두 삭제하고 새로 설정합니다.
            - seasons가 전달될 경우 기존 계절을 모두 삭제하고 새로 설정합니다.
            - brandName, material, size, memo를 빈 문자열("")로 전달하면 null로 설정됩니다.
            - 본인 아이템만 수정할 수 있습니다.
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "아이템 수정 성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (유효하지 않은 색상, 계절, 소재 등)",
            content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
            responseCode = "404",
            description = "아이템을 찾을 수 없음",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "ITEM_NOT_FOUND",
                            value = """
                            {
                              "success": false,
                              "code": "ITEM4042",
                              "message": "존재하지 않거나 접근할 수 없는 아이템입니다.",
                              "timestamp": "2026-01-24T15:00:00"
                            }
                            """
                    )
            )
    )
    CommonResponse<Void> updateItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId,
            @RequestBody(
                    description = "아이템 수정 요청 (모든 필드 선택)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ItemRequestDTO.UpdateItem.class),
                            examples = @ExampleObject(
                                    name = "아이템 수정 예시",
                                    value = """
                                    {
                                      "imageUrl": "https://example.com/new-image.png",
                                      "imageBackground": "GRAY",
                                      "categoryId": 7,
                                      "colors": ["RED"],
                                      "seasons": ["WINTER"],
                                      "brandName": "아디다스",
                                      "material": "울",
                                      "size": "L",
                                      "price": 80000,
                                      "memo": "따뜻한 니트"
                                    }
                                    """
                            )
                    )
            )
            @Valid ItemRequestDTO.UpdateItem request
    );

    @Operation(
            summary = "아이템 삭제",
            description = """
            아이템을 삭제합니다.

            **삭제 시 처리:**
            - 해당 아이템이 포함된 모든 OOTD의 status가 ABNORMAL로 변경됩니다.
            - 아이템과 연관된 데이터(OOTDItem, ClosetItem, ItemUsage, ItemsColor 등)도 함께 삭제됩니다.
            - 본인 아이템만 삭제할 수 있습니다.
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "아이템 삭제 성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "아이템을 찾을 수 없음",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "ITEM_NOT_FOUND",
                            value = """
                            {
                              "success": false,
                              "code": "ITEM4042",
                              "message": "존재하지 않거나 접근할 수 없는 아이템입니다.",
                              "timestamp": "2026-01-24T15:00:00"
                            }
                            """
                    )
            )
    )
    CommonResponse<Void> deleteItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId
    );

    @Operation(
            summary = "아이템이 포함된 OOTD 목록 조회",
            description = """
            특정 아이템이 포함된 OOTD 목록을 조회합니다.
            본인 아이템만 조회할 수 있습니다.
            status = NORMAL인 OOTD만 반환합니다.
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "OOTD 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ItemResponseDTO.ItemOOTDsResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "아이템을 찾을 수 없음",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "ITEM_NOT_FOUND",
                            value = """
                            {
                              "success": false,
                              "code": "ITEM4042",
                              "message": "존재하지 않거나 접근할 수 없는 아이템입니다.",
                              "timestamp": "2026-01-24T15:00:00"
                            }
                            """
                    )
            )
    )
    CommonResponse<ItemResponseDTO.ItemOOTDsResponse> getItemOOTDs(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId
    );
}
