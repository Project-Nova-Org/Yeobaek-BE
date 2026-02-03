package com.nova.yeobaek.domain.ootd.controller;

import com.nova.yeobaek.domain.ootd.controller.docs.OOTDControllerDocs;
import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;
import com.nova.yeobaek.domain.ootd.dto.response.OOTDResponseDTO.CreateOOTDResponse;
import com.nova.yeobaek.domain.ootd.dto.response.OOTDResponseDTO.OOTDDetailResponse;
import com.nova.yeobaek.domain.ootd.dto.response.OOTDResponseDTO.OOTDListResponse;
import com.nova.yeobaek.domain.ootd.service.OOTDService;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springdoc.core.annotations.ParameterObject;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ootds")
public class OOTDController implements OOTDControllerDocs {

    private final OOTDService ootdService;

    /** OOTD 등록 */
    @Override
    @PostMapping
    public CommonResponse<CreateOOTDResponse> createOOTD(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OOTDRequestDTO.CreateOOTD requestDTO
    ) {
        Long ootdId = ootdService.createOOTD(
                userDetails.getUser(),
                requestDTO
        );

        return CommonResponse.onCreated(new CreateOOTDResponse(ootdId));
    }

    /** OOTD 즐겨찾기 */
    @PatchMapping("/{ootdId}/favorite")
    public CommonResponse<Void> toggleFavorite(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long ootdId
    ) {
        ootdService.toggleFavorite(userDetails.getUser(), ootdId);
        return CommonResponse.onSuccess(null);
    }

    /** OOTD 목록 조회 */
    @Override
    @GetMapping
    public CommonResponse<OOTDListResponse> getOOTDList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject @Valid @ModelAttribute OOTDRequestDTO.OOTDSearchCondition condition
    ) {
        OOTDListResponse response = ootdService.getOOTDList(
                userDetails.getUser(),
                condition
        );

        return CommonResponse.onSuccess(response);
    }

    /** OOTD 상세 조회 */
    @Override
    @GetMapping("/{ootdId}")
    public CommonResponse<OOTDDetailResponse> getOOTDDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long ootdId
    ) {
        OOTDDetailResponse response =
                ootdService.getOOTDDetail(userDetails.getUser(), ootdId);

        return CommonResponse.onSuccess(response);
    }

    /** OOTD 수정 */
    @Override
    @PatchMapping("/{ootdId}")
    public CommonResponse<Void> updateOOTD(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long ootdId,
            @Valid @RequestBody OOTDRequestDTO.UpdateOOTD requestDTO
    ) {
        ootdService.updateOOTD(
                userDetails.getUser(),
                ootdId,
                requestDTO
        );

        return CommonResponse.onSuccess(null);
    }

    /** OOTD 삭제 */
    @Override
    @DeleteMapping("/{ootdId}")
    public CommonResponse<Void> deleteOOTD(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long ootdId
    ) {
        ootdService.deleteOOTD(
                userDetails.getUser(),
                ootdId
        );

        return CommonResponse.onSuccess(null);
    }
}