package com.nova.yeobaek.domain.closet.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nova.yeobaek.domain.closet.domain.Closet;
import com.nova.yeobaek.domain.closet.dto.request.ClosetRequestDTO;
import com.nova.yeobaek.domain.closet.repository.ClosetRepository;
import com.nova.yeobaek.domain.closet.status.ClosetErrorStatus;
import com.nova.yeobaek.domain.item.repository.ItemRepository;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.payload.exception.GeneralException;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClosetService {

    private final ClosetRepository closetRepository;
    private final ItemRepository itemRepository;

    public Long create(User user, ClosetRequestDTO.Create request) {


        if (!request.name().matches("^[a-zA-Z0-9가-힣\\s]+$")) {
            throw new GeneralException(ClosetErrorStatus.INVALID_NAME);
        }
        if (closetRepository.existsByUserAndName(user, request.name())) {
            throw new GeneralException(ClosetErrorStatus.DUPLICATED_NAME);
        }
        Set<Long> itemIdSet = new HashSet<>();

        request.items().forEach(item -> {
            Long itemId = item.itemId();

            // 4-1️⃣ 중복 itemId
            if (!itemIdSet.add(itemId)) {
                throw new GeneralException(ClosetErrorStatus.DUPLICATED_ITEM_ID);
            }

            // 4-2️⃣ 존재하지 않는 itemId
            if (!itemRepository.existsById(itemId)) {
                throw new GeneralException(ClosetErrorStatus.ITEM_NOT_FOUND);
            }
        });

        Closet closet = Closet.create(user, request.name(), request.imageUrl());
        return closetRepository.save(closet).getId();
    }
}
