package com.nova.yeobaek.domain.ootd.controller.docs;

import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;
import com.nova.yeobaek.domain.ootd.dto.response.CreateOOTDResponse;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "OOTD 등록 성공",
            content = @Content(
                    schema = @Schema(implementation = CommonResponse.class)
            )
    )
    CommonResponse<CreateOOTDResponse> createOOTD(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OOTDRequestDTO requestDTO
    );
}