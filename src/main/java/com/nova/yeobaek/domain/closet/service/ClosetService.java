
package com.nova.yeobaek.domain.closet.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nova.yeobaek.domain.closet.converter.ClosetConverter;
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
    private final ClosetConverter closetConverter;

    public Long create(User user, ClosetRequestDTO.Create request) {

        // (선택) 빠른 사전 중복 체크: UX용
        // 동시성 완전 방지는 DB 유니크 제약 + catch 로 처리
        if (closetRepository.existsByUserAndName(user, request.name())) {
            throw new GeneralException(ClosetErrorStatus.DUPLICATED_NAME);
        }

        Set<Long> itemIdSet = new HashSet<>();
        request.items().forEach(item -> {
            Long itemId = item.itemId();


            if (!itemIdSet.add(itemId)) {
                throw new GeneralException(ClosetErrorStatus.DUPLICATED_ITEM_ID);
            }


            if (!itemRepository.existsById(itemId)) {
                throw new GeneralException(ClosetErrorStatus.ITEM_NOT_FOUND);
            }
        });

        Closet closet = closetConverter.toEntity(user, request);

        try {
            return closetRepository.save(closet).getId();
        } catch (DataIntegrityViolationException e) {

            throw new GeneralException(ClosetErrorStatus.DUPLICATED_NAME);
        }
    }
}
