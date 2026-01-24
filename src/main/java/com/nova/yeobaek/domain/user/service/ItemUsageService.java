package com.nova.yeobaek.domain.user.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nova.yeobaek.domain.ootd.domain.mapping.OOTDItem;
import com.nova.yeobaek.domain.ootd.repository.ootdItem.OOTDItemRepository;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.mapping.ItemUsage;
import com.nova.yeobaek.domain.user.repository.itemUsage.ItemUsageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemUsageService {

    private final ItemUsageRepository itemUsageRepository;
    private final OOTDItemRepository ootdItemRepository;

    @Transactional
    public void increaseByOotd(User user, Long ootdId, LocalDate date) {
        if (ootdId == null) return;

        List<OOTDItem> ootdItems = ootdItemRepository.findAllByOotd_Id(ootdId);

        for (OOTDItem oi : ootdItems) {
            Long itemId = oi.getItem().getId();

            ItemUsage usage = itemUsageRepository
                    .findByUser_IdAndItem_Id(user.getId(), itemId)
                    .orElseGet(() -> itemUsageRepository.save(
                            ItemUsage.builder()
                                    .user(user)
                                    .item(oi.getItem())
                                    .useCount(0)
                                    .build()
                    ));

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
}
