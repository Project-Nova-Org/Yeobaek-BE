package com.nova.yeobaek.domain.ootd.controller;

import com.nova.yeobaek.domain.ootd.controller.docs.OOTDControllerDocs;
import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;
import com.nova.yeobaek.domain.ootd.dto.response.CreateOOTDResponse;
import com.nova.yeobaek.domain.ootd.dto.response.OOTDDetailResponse;
import com.nova.yeobaek.domain.ootd.dto.response.OOTDListResponse;
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

import java.util.List;

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
            @Valid @RequestBody OOTDRequestDTO.Create requestDTO
    ) {
        Long ootdId = ootdService.createOOTD(
                userDetails.getUser(),
                requestDTO
        );

        return CommonResponse.onCreated(new CreateOOTDResponse(ootdId));
    }

    /** OOTD 목록 조회 */
    @Override
    @GetMapping
    public CommonResponse<OOTDListResponse> getOOTDList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject @Valid @ModelAttribute OOTDRequestDTO.SearchCondition condition
    ) {
        List<Long> safeTpoIds =
                (condition.tpoId() == null || condition.tpoId().isEmpty())
                        ? null
                        : condition.tpoId();

        List<Long> safeStyleIds =
                (condition.styleId() == null || condition.styleId().isEmpty())
                        ? null
                        : condition.styleId();

        OOTDListResponse response = ootdService.getOOTDList(
                userDetails.getUser(),
                condition.keyword(),
                condition.favorite(),
                safeTpoIds,
                safeStyleIds,
                condition.sort() != null ? condition.sort() : "LATEST",
                condition.cursor(),
                condition.limit() != null ? condition.limit() : 20
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
}