package com.nova.yeobaek.domain.ootd.controller.docs;

import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;
import com.nova.yeobaek.domain.ootd.dto.response.CreateOOTDResponse;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "OOTD", description = "오늘의 착장 API")
public interface OOTDControllerDocs {

    @Operation(
            summary = "OOTD 등록",
            description = """
            새로운 OOTD를 생성합니다.

            본 API는 공통 응답(CommonResponse) 형식을 사용합니다.
            """
    )
    @ApiResponse(
            responseCode = "201",
            description = "OOTD 등록 성공",
            content = @Content(
                    schema = @Schema(implementation = CommonResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "유효하지 않은 이미지 배경 색상",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "INVALID_IMAGE_BACKGROUND",
                            value = """
                            {
                              "success": false,
                              "code": "OOTD4002",
                              "message": "유효하지 않은 이미지 배경 색상입니다.",
                              "timestamp": "2026-01-13T15:12:06.689497"
                            }
                            """
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "아이템 ID 중복",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "DUPLICATED_ITEM_ID",
                            value = """
                            {
                              "success": false,
                              "code": "OOTD4001",
                              "message": "아이템 ID가 중복되었습니다.",
                              "timestamp": "2026-01-13T15:09:03.991"
                            }
                            """
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 스타일",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "STYLE_NOT_FOUND",
                            value = """
                            {
                              "success": false,
                              "code": "OOTD4041",
                              "message": "존재하지 않는 스타일입니다.",
                              "timestamp": "2026-01-13T15:08:28.878"
                            }
                            """
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 TPO",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "TPO_NOT_FOUND",
                            value = """
                        {
                          "success": false,
                          "code": "OOTD4042",
                          "message": "존재하지 않는 TPO입니다.",
                          "timestamp": "2026-01-13T15:11:10.123456"
                        }
                        """
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 아이템",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "ITEM_NOT_FOUND",
                            value = """
                            {
                              "success": false,
                              "code": "OOTD4043",
                              "message": "존재하지 않는 아이템이 포함되어 있습니다.",
                              "timestamp": "2026-01-13T15:10:12.221"
                            }
                            """
                    )
            )
    )
    CommonResponse<CreateOOTDResponse> createOOTD(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OOTDRequestDTO.Create requestDTO
    );
}