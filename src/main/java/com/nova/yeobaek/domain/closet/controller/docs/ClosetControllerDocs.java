package com.nova.yeobaek.domain.closet.controller.docs;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.nova.yeobaek.domain.closet.dto.request.ClosetRequestDTO;
import com.nova.yeobaek.domain.closet.dto.type.ClosetSortType;
import com.nova.yeobaek.domain.closet.dto.response.ClosetResponseDTO;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Closet", description = "옷장 API")
public interface ClosetControllerDocs {

    @Operation(
            summary = "옷장 생성 API",
            description = "로그인한 사용자의 옷장을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "옷장 생성 성공"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "이름에 특수문자 포함",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "INVALID_NAME", value = """
                        {
                          "success": false,
                          "code": "CLOSET_4001",
                          "message": "특수문자는 사용할 수 없습니다.",
                          "timestamp": "2026-01-14T10:00:00"
                        }
                        """))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "아이템 미포함",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "EMPTY_ITEMS", value = """
                        {
                          "success": false,
                          "code": "CLOSET_4002",
                          "message": "하나 이상의 아이템이 포함되어야 합니다.",
                          "timestamp": "2026-01-14T10:00:00"
                        }
                        """))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UNAUTHORIZED", value = """
                        {
                          "success": false,
                          "code": "AUTH_401",
                          "message": "로그인이 필요합니다."
                        }
                        """))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "SERVER_ERROR", value = """
                        {
                          "success": false,
                          "code": "CLOSET_5001",
                          "message": "서버 오류 발생."
                        }
                        """))
                    )
            }
    )
    CommonResponse<ClosetResponseDTO.Create> createCloset(User user, ClosetRequestDTO.Create request);

    @Operation(
            summary = "옷장 리스트 조회 API",
            description = "로그인한 사용자의 옷장 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetResponseDTO.CursorListResponse> getClosets(
            User user,
            @Parameter(description = "이전 페이지 마지막 closetId(없으면 첫 페이지)")
            @RequestParam(required = false) Long cursorId,

            @Parameter(description = "이전 페이지 마지막 favorite(복합정렬 시 필요)")
            @RequestParam(required = false) Boolean cursorFavorite,

            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "20") Integer size,

            @Parameter(description = "정렬 방식")
            @RequestParam(defaultValue = "LATEST") ClosetSortType sort
    );

    @Operation(
            summary = "옷장 상세 조회 API",
            description = "로그인한 사용자의 특정 옷장을 상세 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "옷장 없음(또는 접근 불가)",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "CLOSET_NOT_FOUND", value = """
                        {
                          "success": false,
                          "code": "CLOSET_4041",
                          "message": "옷장을 찾을 수 없습니다.",
                          "timestamp": "2026-01-28T10:00:00"
                        }
                        """))
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetResponseDTO.Detail> getClosetDetail(
            User user,
            @PathVariable Long closetId
    );

    @Operation(
            summary = "옷장 즐겨찾기 설정/해제 API",
            description = "로그인한 사용자의 옷장 즐겨찾기를 설정/해제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "404", description = "옷장 없음(또는 접근 불가)"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetResponseDTO.FavoriteUpdate> updateFavorite(
            User user,
            @PathVariable Long closetId,
            ClosetRequestDTO.FavoriteUpdate request
    );

    @Operation(
            summary = "옷장 아이템 목록 조회 API",
            description = "특정 옷장에 포함된 아이템을 커서 기반으로 조회합니다. 카테고리(종류/세부분류) 필터를 지원합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "옷장 없음(또는 접근 불가)",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "CLOSET_NOT_FOUND", value = """
                        {
                          "success": false,
                          "code": "CLOSET_4041",
                          "message": "옷장을 찾을 수 없습니다.",
                          "timestamp": "2026-01-28T10:00:00"
                        }
                        """))
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetResponseDTO.ClosetItemCursorListResponse> getClosetItems(
            User user,

            @Parameter(description = "옷장 ID")
            @PathVariable Long closetId,

            @Parameter(description = "이전 페이지 마지막 closetItemId(없으면 첫 페이지)")
            @RequestParam(required = false) Long cursorId,

            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "20") Integer size,

            @Parameter(description = "카테고리 종류(대분류) ID")
            @RequestParam(required = false) Long level1CategoryId,

            @Parameter(description = "카테고리 세부분류(소분류) ID")
            @RequestParam(required = false) Long level2CategoryId
    );
}
