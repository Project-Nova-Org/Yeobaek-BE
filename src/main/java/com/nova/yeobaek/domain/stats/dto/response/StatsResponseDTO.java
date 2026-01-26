package com.nova.yeobaek.domain.stats.dto.response;

import java.time.LocalDate;
import java.util.List;

public class StatsResponseDTO {

    /** 아이템 카드 */
    public record ItemUsageCard(
            Long itemId,
            String brandName,
            String imageUrl,
            int useCount,
            LocalDate lastUsedDate
    ) {}

    /** 화면 요약 응답 */
    public record ItemsSummaryResponse(
            List<ItemUsageCard> frequentItems,
            List<ItemUsageCard> inactiveItemsPreview,
            int inactiveItemsExtraCount
    ) {}
}
