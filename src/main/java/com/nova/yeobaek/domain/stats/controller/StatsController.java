package com.nova.yeobaek.domain.stats.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nova.yeobaek.domain.stats.controller.docs.StatsControllerDocs;
import com.nova.yeobaek.domain.stats.dto.response.StatsResponseDTO;
import com.nova.yeobaek.domain.stats.service.StatsService;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatsController implements StatsControllerDocs {

    private final StatsService statsService;

    @Override
    @GetMapping("/items")
    public CommonResponse<StatsResponseDTO.ItemsSummaryResponse> getItemsSummary(
            @AuthenticationPrincipal(expression = "user") User user,
            @RequestParam(defaultValue = "10") int frequentLimit,
            @RequestParam(defaultValue = "3") int inactivePreviewLimit,
            @RequestParam(defaultValue = "30") int inactiveDays
    ) {
        return CommonResponse.onSuccess(
                statsService.getItemsSummary(
                        user.getId(),
                        frequentLimit,
                        inactivePreviewLimit,
                        inactiveDays
                )
        );
    }
}
