package com.nova.yeobaek.domain.user.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.ootd.domain.mapping.OOTDItem;
import com.nova.yeobaek.domain.ootd.repository.ootdItem.OOTDItemRepository;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.mapping.ItemUsage;
import com.nova.yeobaek.domain.user.repository.itemUsage.ItemUsageRepository;
import com.nova.yeobaek.domain.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemUsageService {

    private final ItemUsageRepository itemUsageRepository;
    private final OOTDItemRepository ootdItemRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void increaseByOotd(User user, Long ootdId, LocalDate date) {
        if (ootdId == null) return;

        List<OOTDItem> ootdItems = ootdItemRepository.findAllByOotd_Id(ootdId);

        for (OOTDItem oi : ootdItems) {
            Long itemId = oi.getItem().getId();

            ItemUsage usage = getOrCreateUsage(user, oi.getItem());
            usage.increase(date);
        }
    }

    @Transactional
    public void decreaseByOotd(User user, Long ootdId) {
        if (ootdId == null) return;

        List<OOTDItem> ootdItems = ootdItemRepository.findAllByOotd_Id(ootdId);

        for (OOTDItem oi : ootdItems) {
            Long itemId = oi.getItem().getId();

            itemUsageRepository.findByUser_IdAndItem_Id(user.getId(), itemId)
                    .ifPresent(ItemUsage::decrease);
        }
    }

    @Transactional
    public void increase(User user, Long itemId, int count) {
        if (count <= 0) return;

        Item itemRef = itemRepository.getReferenceById(itemId);
        ItemUsage usage = getOrCreateUsage(user, itemRef);

        usage.increase(count);
    }

    @Transactional
    public void decrease(User user, Long itemId, int count) {
        if (count <= 0) return;

        itemUsageRepository.findByUser_IdAndItem_Id(user.getId(), itemId)
                .ifPresent(usage -> usage.decrease(count));
    }

    /**
     * ✅ 동시성 안전 get-or-create
     * - find 후 없으면 save 시도
     * - uk_user_item 유니크 충돌 발생 시(다른 트랜잭션이 먼저 생성) 재조회하여 반환
     */
    private ItemUsage getOrCreateUsage(User user, Item item) {

        Long itemId = item.getId();

        return itemUsageRepository.findByUser_IdAndItem_Id(user.getId(), itemId)
                .orElseGet(() -> {
                    try {
                        return itemUsageRepository.save(
                                ItemUsage.builder()
                                        .user(user)
                                        .item(item)
                                        .useCount(0)
                                        .build()
                        );
                    } catch (DataIntegrityViolationException e) {
                        // 동시 생성 레이스 → 기존 row 재조회
                        return itemUsageRepository.findByUser_IdAndItem_Id(user.getId(), itemId)
                                .orElseThrow(() -> e);
                    }
                });
    }
}
