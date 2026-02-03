package com.nova.yeobaek.domain.stats.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.item.repository.ItemRepository;
import com.nova.yeobaek.domain.stats.dto.response.StatsResponseDTO;
import com.nova.yeobaek.domain.user.domain.mapping.ItemUsage;
import com.nova.yeobaek.domain.user.repository.itemUsage.ItemUsageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final ItemRepository itemRepository;
    private final ItemUsageRepository itemUsageRepository;

    public StatsResponseDTO.ItemsSummaryResponse getItemsSummary(
            Long userId,
            int frequentLimit,
            int inactivePreviewLimit,
            int inactiveDays
    ) {
        // 1) 유저 보유 아이템 전체
        List<Item> items = itemRepository.findAllByUser_Id(userId);

        // 2) 아이템 사용 정보 (useCount DESC)
        List<ItemUsage> usages = itemUsageRepository.findAllByUser_IdOrderByUseCountDesc(userId);

        // itemId -> usage 맵
        Map<Long, ItemUsage> usageMap = usages.stream()
                .collect(Collectors.toMap(
                        u -> u.getItem().getId(),
                        u -> u,
                        (a, b) -> a
                ));

        // 3) 자주 착용한 아이템 (전체 items 기준으로 일관되게)
        List<StatsResponseDTO.ItemUsageCard> frequentItems = items.stream()
                .map(item -> toCard(item, usageMap.get(item.getId())))
                .filter(c -> c.useCount() > 0)
                .sorted(Comparator.comparingInt(StatsResponseDTO.ItemUsageCard::useCount).reversed())
                .limit(frequentLimit)
                .toList();

        // frequent에 포함된 itemId Set → inactive에서 제외
        Set<Long> frequentItemIds = frequentItems.stream()
                .map(StatsResponseDTO.ItemUsageCard::itemId)
                .collect(Collectors.toSet());

        // 4) 최근 착용하지 않은 아이템
        LocalDate cutoff = LocalDate.now().minusDays(inactiveDays);

        List<StatsResponseDTO.ItemUsageCard> inactiveAll = items.stream()
                .map(item -> toCard(item, usageMap.get(item.getId())))
                .filter(card -> !frequentItemIds.contains(card.itemId()))
                .filter(card ->
                        card.lastUsedDate() == null ||
                                card.lastUsedDate().isBefore(cutoff) // cutoff 당일은 inactive 아님
                )
                .sorted(Comparator
                        .comparing((StatsResponseDTO.ItemUsageCard c) -> c.lastUsedDate() != null)
                        .thenComparing(
                                StatsResponseDTO.ItemUsageCard::lastUsedDate,
                                Comparator.nullsFirst(Comparator.naturalOrder())
                        )
                )
                .toList();

        List<StatsResponseDTO.ItemUsageCard> inactivePreview = inactiveAll.stream()
                .limit(inactivePreviewLimit)
                .toList();

        int extraCount = Math.max(0, inactiveAll.size() - inactivePreview.size());

        return new StatsResponseDTO.ItemsSummaryResponse(
                frequentItems,
                inactivePreview,
                extraCount
        );
    }

    private StatsResponseDTO.ItemUsageCard toCard(ItemUsage usage) {
        Item item = usage.getItem();
        return new StatsResponseDTO.ItemUsageCard(
                item.getId(),
                item.getBrandName(),
                item.getImageUrl(),
                usage.getUseCount(),
                usage.getLastUsedDate()
        );
    }

    private StatsResponseDTO.ItemUsageCard toCard(Item item, ItemUsage usage) {
        return new StatsResponseDTO.ItemUsageCard(
                item.getId(),
                item.getBrandName(),
                item.getImageUrl(),
                usage == null ? 0 : usage.getUseCount(),
                usage == null ? null : usage.getLastUsedDate()
        );
    }
}
