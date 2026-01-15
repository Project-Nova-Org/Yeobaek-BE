package com.nova.yeobaek.domain.ootd.controller.docs;

import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;
import com.nova.yeobaek.domain.ootd.dto.response.CreateOOTDResponse;
import com.nova.yeobaek.domain.ootd.dto.response.OOTDDetailResponse;
import com.nova.yeobaek.domain.ootd.dto.response.OOTDListResponse;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "OOTD", description = "오늘의 착장 API")
public interface OOTDControllerDocs {

    /** OOTD 등록 */
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
            content = @Content(schema = @Schema(implementation = CommonResponse.class))
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
            description = "존재하지 않는 스타일 / TPO / 아이템",
            content = @Content(mediaType = "application/json")
    )
    CommonResponse<CreateOOTDResponse> createOOTD(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OOTDRequestDTO.Create requestDTO
    );

    /** OOTD 목록조회 */
    @Operation(
            summary = "OOTD 목록 조회",
            description = """
            로그인 사용자의 OOTD 목록을 조회합니다.
            status = NORMAL 인 OOTD만 반환합니다.
            cursor 기반 무한 스크롤을 지원합니다.

            - sort 기본값: LATEST
            - limit 기본값: 20 (최대 100)
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "OOTD 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = OOTDListResponse.class))
    )
    CommonResponse<OOTDListResponse> getOOTDList(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(
                    description = "OOTD 목록 조회 조건 (기본값: sort=LATEST, limit=20)",
                    schema = @Schema(
                            defaultValue = "{\"sort\":\"LATEST\",\"limit\":20}"
                    )
            )
            @ParameterObject
            @Valid
            @ModelAttribute
            OOTDRequestDTO.SearchCondition condition
    );

    /** OOTD 상세 조회 */
    @Operation(
            summary = "OOTD 상세 조회",
            description = """
            로그인 사용자의 OOTD 상세 정보를 조회합니다.
            status = NORMAL 인 OOTD만 조회됩니다.
            OOTD 기본 정보와 OOTDItem 목록을 함께 반환합니다.
            """

    )
    @ApiResponse(responseCode = "200", description = "OOTD 상세 조회 성공")
    CommonResponse<OOTDDetailResponse> getOOTDDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long ootdId
    );

    /** OOTD 수정 */
    @Operation(
            summary = "OOTD 수정",
            description = """
            로그인 사용자의 OOTD 정보를 수정합니다.
            PATCH 방식으로 부분 수정이 가능하며,
            전달되지 않은 필드는 기존 값을 유지합니다.

            - 아이템 목록이 전달될 경우 전체 교체 방식으로 수정됩니다.
            - 본인 OOTD만 수정할 수 있습니다.
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "OOTD 수정 성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않거나 접근할 수 없는 OOTD",
            content = @Content(mediaType = "application/json")
    )
    CommonResponse<Void> updateOOTD(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long ootdId,
            @Valid @RequestBody OOTDRequestDTO.Update requestDTO
    );

    /** OOTD 삭제 */
    @Operation(
            summary = "OOTD 삭제",
            description = """
            로그인 사용자의 OOTD를 삭제합니다.
            실제 데이터는 삭제되지 않으며,
            status 값을 변경하는 논리 삭제 방식으로 처리됩니다.

            - 본인 OOTD만 삭제할 수 있습니다.
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "OOTD 삭제 성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "존재하지 않거나 접근할 수 없는 OOTD",
            content = @Content(mediaType = "application/json")
    )
    CommonResponse<Void> deleteOOTD(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long ootdId
    );
}