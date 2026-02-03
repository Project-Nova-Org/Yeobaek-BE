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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            @Valid @RequestBody ItemRequestDTO.CreateItem request
    ) {
        ItemResponseDTO.CreateResponse response = itemService.createItem(
                userDetails.getUser(),
                request
        );

        return CommonResponse.onCreated(response);
    }

    @Override
    @GetMapping
    public CommonResponse<ItemResponseDTO.ListResponse> getItemList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute ItemRequestDTO.ItemSearchCondition condition
    ) {
        ItemResponseDTO.ListResponse response = itemService.getItemList(
                userDetails.getUser(),
                condition
        );

        return CommonResponse.onSuccess(response);
    }

    @Override
    @GetMapping("/{itemId}")
    public CommonResponse<ItemResponseDTO.DetailResponse> getItemDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId
    ) {
        ItemResponseDTO.DetailResponse response = itemService.getItemDetail(
                userDetails.getUser(),
                itemId
        );

        return CommonResponse.onSuccess(response);
    }

    @Override
    @PatchMapping("/{itemId}")
    public CommonResponse<Void> updateItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId,
            @Valid @RequestBody ItemRequestDTO.UpdateItem request
    ) {
        itemService.updateItem(
                userDetails.getUser(),
                itemId,
                request
        );

        return CommonResponse.onSuccess(null);
    }

    @Override
    @DeleteMapping("/{itemId}")
    public CommonResponse<Void> deleteItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId
    ) {
        itemService.deleteItem(
                userDetails.getUser(),
                itemId
        );

        return CommonResponse.onSuccess(null);
    }

    @Override
    @GetMapping("/{itemId}/ootds")
    public CommonResponse<ItemResponseDTO.ItemOOTDsResponse> getItemOOTDs(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId
    ) {
        ItemResponseDTO.ItemOOTDsResponse response = itemService.getItemOOTDs(
                userDetails.getUser(),
                itemId
        );

        return CommonResponse.onSuccess(response);
    }
}
