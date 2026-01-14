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

                    // ✅ 성공
                    @ApiResponse(
                            responseCode = "201",
                            description = "옷장 생성 성공"
                    ),

                    // ❌ 400 - 이름 특수문자
                    @ApiResponse(
                            responseCode = "400",
                            description = "이름에 특수문자 포함",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "INVALID_NAME",
                                            value = """
                        {
                          "success": false,
                          "code": "CLOSET_4001",
                          "message": "특수문자는 사용할 수 없습니다.",
                          "timestamp": "2026-01-14T10:00:00"
                        }
                        """
                                    )
                            )
                    ),

                    // ❌ 400 - 아이템 없음
                    @ApiResponse(
                            responseCode = "400",
                            description = "아이템 미포함",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "EMPTY_ITEMS",
                                            value = """
                        {
                          "success": false,
                          "code": "CLOSET_4002",
                          "message": "하나 이상의 아이템이 포함되어야 합니다.",
                          "timestamp": "2026-01-14T10:00:00"
                        }
                        """
                                    )
                            )
                    ),

                    // ❌ 401
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "UNAUTHORIZED",
                                            value = """
                        {
                          "success": false,
                          "code": "AUTH_401",
                          "message": "로그인이 필요합니다."
                        }
                        """
                                    )
                            )
                    ),

                    // ❌ 500
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "SERVER_ERROR",
                                            value = """
                        {
                          "success": false,
                          "code": "CLOSET_5001",
                          "message": "서버 오류 발생."
                        }
                        """
                                    )
                            )
                    )
            }
    )
    CommonResponse<ClosetResponseDTO.Create> createCloset(
            User user,
            ClosetRequestDTO.Create request
    );
}
