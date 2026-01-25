package com.nova.yeobaek.domain.stats.controller.docs;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import com.nova.yeobaek.domain.stats.dto.response.StatsResponseDTO;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Stats", description = "통계 API")
public interface StatsControllerDocs {

    @Operation(
            summary = "아이템 착용 통계 요약 조회",
            description = """
                    - 자주 착용한 아이템 목록
                    - 최근 착용하지 않은 아이템(미착용 포함)
                    - 미리보기 외 나머지 개수 제공
                    """
    )
    CommonResponse<StatsResponseDTO.ItemsSummaryResponse> getItemsSummary(
            @AuthenticationPrincipal User user,

            @Parameter(description = "자주 착용한 아이템 개수", example = "10")
            @RequestParam(defaultValue = "10") int frequentLimit,

            @Parameter(description = "최근 착용하지 않은 아이템 미리보기 개수", example = "3")
            @RequestParam(defaultValue = "3") int inactivePreviewLimit,

            @Parameter(description = "미착용 기준 일수", example = "30")
            @RequestParam(defaultValue = "30") int inactiveDays
    );
}
