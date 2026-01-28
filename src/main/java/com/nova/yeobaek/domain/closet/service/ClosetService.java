package com.nova.yeobaek.domain.closet.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nova.yeobaek.domain.closet.converter.ClosetConverter;
import com.nova.yeobaek.domain.closet.domain.Closet;
import com.nova.yeobaek.domain.closet.domain.mapping.ClosetItem;
import com.nova.yeobaek.domain.closet.dto.request.ClosetRequestDTO;
import com.nova.yeobaek.domain.closet.dto.response.ClosetResponseDTO;
import com.nova.yeobaek.domain.closet.repository.ClosetRepository;
import com.nova.yeobaek.domain.closet.repository.closetItem.ClosetItemRepository;
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
    private final ClosetItemRepository closetItemRepository; // === added ===
    private final ClosetConverter closetConverter;

    public Long create(User user, ClosetRequestDTO.Create request) {

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
            Closet saved = closetRepository.save(closet);

            // === added: closet_items 저장 (placement 포함) ===
            List<ClosetItem> closetItems = request.items().stream()
                    .map(p -> ClosetItem.builder()
                            .closet(saved)
                            .item(itemRepository.getReferenceById(p.itemId()))
                            .posX(p.posX())
                            .posY(p.posY())
                            .scale(p.scale())
                            .rotation(p.rotation())
                            .zIndex(p.zIndex())
                            .build()
                    )
                    .toList();

            closetItemRepository.saveAll(closetItems);

            return saved.getId();
        } catch (DataIntegrityViolationException e) {
            throw new GeneralException(ClosetErrorStatus.DUPLICATED_NAME);
        }
    }


    @Transactional(readOnly = true)
    public ClosetResponseDTO.ListResponse list(User user) {
        List<ClosetResponseDTO.Summary> closets = closetRepository.findAllByUserOrderByIdDesc(user).stream()
                .map(closetConverter::toSummary)
                .toList();
        return new ClosetResponseDTO.ListResponse(closets);
    }


    @Transactional(readOnly = true)
    public ClosetResponseDTO.Detail getDetail(User user, Long closetId) {
        Closet closet = closetRepository.findByIdAndUser(closetId, user)
                .orElseThrow(() -> new GeneralException(ClosetErrorStatus.CLOSET_NOT_FOUND));

        List<ClosetItem> closetItems = closetItemRepository.findAllWithItemByClosetId(closetId);
        return closetConverter.toDetail(closet, closetItems);
    }
}
