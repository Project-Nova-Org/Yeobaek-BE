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
        List<Item> items = itemRepository.findAllByUser_Id(userId);
        List<ItemUsage> usages = itemUsageRepository.findAllByUser_IdOrderByUseCountDesc(userId);

        Map<Long, ItemUsage> usageMap = usages.stream()
                .collect(Collectors.toMap(
                        u -> u.getItem().getId(),
                        u -> u,
                        (a, b) -> a
                ));

        List<StatsResponseDTO.ItemUsageCard> frequentItems = items.stream()
                .map(item -> toCard(item, usageMap.get(item.getId())))
                .filter(c -> c.useCount() > 0)
                .sorted(Comparator.comparingInt(StatsResponseDTO.ItemUsageCard::useCount).reversed())
                .limit(frequentLimit)
                .toList();

        Set<Long> frequentItemIds = frequentItems.stream()
                .map(StatsResponseDTO.ItemUsageCard::itemId)
                .collect(Collectors.toSet());

        LocalDate cutoff = LocalDate.now().minusDays(inactiveDays);

        List<StatsResponseDTO.ItemUsageCard> inactiveAll = items.stream()
                .map(item -> toCard(item, usageMap.get(item.getId())))
                .filter(card -> !frequentItemIds.contains(card.itemId()))
                .filter(card ->
                        card.lastUsedDate() == null ||
                                card.lastUsedDate().isBefore(cutoff)
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
                extractBrandName(item),
                item.getImageUrl(),
                usage.getUseCount(),
                usage.getLastUsedDate()
        );
    }

    private StatsResponseDTO.ItemUsageCard toCard(Item item, ItemUsage usage) {
        return new StatsResponseDTO.ItemUsageCard(
                item.getId(),
                extractBrandName(item),
                item.getImageUrl(),
                usage == null ? 0 : usage.getUseCount(),
                usage == null ? null : usage.getLastUsedDate()
        );
    }

    private String extractBrandName(Item item) {
        // ✅ 여기만 네 Item 필드명에 맞게 바꾸면 끝
        return item.getBrandName(); // ← 만약 컴파일 안 되면 아래 4) 참고
    }
}
