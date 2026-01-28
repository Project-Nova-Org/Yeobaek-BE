package com.nova.yeobaek.domain.closet.controller.docs;

import com.nova.yeobaek.domain.closet.dto.request.ClosetRequestDTO;
import com.nova.yeobaek.domain.closet.dto.response.ClosetResponseDTO;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
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

    // === added ===
    @Operation(
            summary = "옷장 리스트 조회 API",
            description = "로그인한 사용자의 옷장 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "401", description = "인증 필요"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    CommonResponse<ClosetResponseDTO.ListResponse> getClosets(User user);

    // === added ===
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
    CommonResponse<ClosetResponseDTO.Detail> getClosetDetail(User user, Long closetId);
}
