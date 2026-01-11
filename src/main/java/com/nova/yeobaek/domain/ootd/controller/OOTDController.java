package com.nova.yeobaek.domain.ootd.controller;

import com.nova.yeobaek.domain.ootd.controller.docs.OOTDControllerDocs;
import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;
import com.nova.yeobaek.domain.ootd.service.OOTDService;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @Valid @RequestBody OOTDRequestDTO requestDTO
    ) {
        Long ootdId = ootdService.createOOTD(requestDTO);
        return CommonResponse.onCreated(new CreateOOTDResponse(ootdId));
    }

    /** OOTD 등록 응답 DTO */
    public record CreateOOTDResponse(Long ootdId) {}
}