package com.nova.yeobaek.domain.ootd.controller;

import com.nova.yeobaek.domain.ootd.controller.docs.OOTDControllerDocs;
import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;
import com.nova.yeobaek.domain.ootd.dto.response.CreateOOTDResponse;
import com.nova.yeobaek.domain.ootd.dto.response.OOTDListResponse;
import com.nova.yeobaek.domain.ootd.service.OOTDService;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.nova.yeobaek.domain.ootd.dto.response.OOTDDetailResponse;

@Slf4j
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
            @Valid @RequestBody OOTDRequestDTO.Create requestDTO
    ) {
        Long ootdId = ootdService.createOOTD(
                userDetails.getUser(),
                requestDTO
        );
        return CommonResponse.onCreated(new CreateOOTDResponse(ootdId));
    }

    /** OOTD 목록조회 */
    @Override
    @GetMapping
    public CommonResponse<OOTDListResponse> getOOTDList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(required = false) List<Long> tpoId,
            @RequestParam(required = false) List<Long> styleId,
            @RequestParam(defaultValue = "LATEST") String sort,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") Integer limit
    ) {
        List<Long> safeTpoIds = (tpoId == null || tpoId.isEmpty()) ? null : tpoId;
        List<Long> safeStyleIds = (styleId == null || styleId.isEmpty()) ? null : styleId;

        OOTDListResponse response = ootdService.getOOTDList(
                userDetails.getUser(),
                keyword,
                favorite,
                safeTpoIds,
                safeStyleIds,
                sort,
                cursor,
                limit
        );

        return CommonResponse.onSuccess(response);
    }

    /** OOTD 상세조회 */
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


}