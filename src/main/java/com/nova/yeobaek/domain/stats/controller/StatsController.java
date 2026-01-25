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
        // ✅ 디버그용 (지금 문제는 여기서 userId가 1로 들어오냐가 핵심)
        System.out.println("[StatsController] user = " + user);
        System.out.println("[StatsController] userId = " + (user == null ? null : user.getId()));
        System.out.println("[StatsController] frequentLimit=" + frequentLimit
                + ", inactivePreviewLimit=" + inactivePreviewLimit
                + ", inactiveDays=" + inactiveDays);

        // ✅ user가 null이면 조용히 빈 배열 만들지 말고 바로 원인 드러내기
        if (user == null || user.getId() == null) {
            throw new IllegalStateException("AuthenticationPrincipal(user)가 null 입니다. (principal 매핑 확인 필요)");
        }

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
