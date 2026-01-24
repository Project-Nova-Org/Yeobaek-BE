package com.nova.yeobaek.domain.item.controller;

import com.nova.yeobaek.domain.item.controller.docs.ItemControllerDocs;
import com.nova.yeobaek.domain.item.dto.request.ItemRequestDTO;
import com.nova.yeobaek.domain.item.dto.response.ItemResponseDTO;
import com.nova.yeobaek.domain.item.service.ItemService;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController implements ItemControllerDocs {

    private final ItemService itemService;

    @Override
    @PostMapping
    public CommonResponse<ItemResponseDTO.CreateResponse> createItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ItemRequestDTO.Create request
    ) {
        ItemResponseDTO.CreateResponse response = itemService.createItem(
                userDetails.getUser(),
                request
        );

        return CommonResponse.onCreated(response);
    }
}
