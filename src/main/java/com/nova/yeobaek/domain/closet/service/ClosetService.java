package com.nova.yeobaek.domain.closet.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nova.yeobaek.domain.closet.converter.ClosetConverter;
import com.nova.yeobaek.domain.closet.domain.Closet;
import com.nova.yeobaek.domain.closet.domain.mapping.ClosetItem;
import com.nova.yeobaek.domain.closet.dto.request.ClosetRequestDTO;
import com.nova.yeobaek.domain.closet.dto.request.ClosetSortType;
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
    private final ClosetConverter closetConverter;

    private final ItemRepository itemRepository;
    private final ClosetItemRepository closetItemRepository;

    public Long create(User user, ClosetRequestDTO.Create request) {
        validateCreateRequest(user, request);

        Closet saved = saveCloset(user, request);
        saveClosetItems(saved, request);

        return saved.getId();
    }

    public ClosetResponseDTO.FavoriteUpdate updateFavorite(User user, Long closetId, boolean favorite) {
        Closet closet = closetRepository.findByIdAndUser(closetId, user)
                .orElseThrow(() -> new GeneralException(ClosetErrorStatus.CLOSET_NOT_FOUND));

        closet.updateFavorite(favorite);

        return new ClosetResponseDTO.FavoriteUpdate(closet.getId(), closet.isFavorite());
    }

    @Transactional(readOnly = true)
    public ClosetResponseDTO.CursorListResponse listByCursor(
            User user,
            Long cursorId,
            Boolean cursorFavorite,
            int size,
            ClosetSortType sort
    ) {
        int limit = size + 1;
        Pageable pageable = PageRequest.of(0, limit);

        if (sort == null) sort = ClosetSortType.LATEST;

        List<Closet> page;

        if (sort == ClosetSortType.FAVORITE_LATEST) {
            if (cursorId == null) {
                page = closetRepository.findByUserOrderByFavoriteDescIdDesc(user, pageable);
            } else {
                int cursorFav = Boolean.TRUE.equals(cursorFavorite) ? 1 : 0;
                page = closetRepository.findByUserAfterFavoriteDescIdDesc(user, cursorFav, cursorId, pageable);
            }

        } else if (sort == ClosetSortType.FAVORITE_OLDEST) {
            if (cursorId == null) {
                page = closetRepository.findByUserOrderByFavoriteDescIdAsc(user, pageable);
            } else {
                int cursorFav = Boolean.TRUE.equals(cursorFavorite) ? 1 : 0;
                page = closetRepository.findByUserAfterFavoriteDescIdAsc(user, cursorFav, cursorId, pageable);
            }

        } else if (sort == ClosetSortType.OLDEST) {
            page = (cursorId == null)
                    ? closetRepository.findByUserOrderByIdAsc(user, pageable)
                    : closetRepository.findByUserAndIdGreaterThanOrderByIdAsc(user, cursorId, pageable);

        } else { // LATEST default
            page = (cursorId == null)
                    ? closetRepository.findByUserOrderByIdDesc(user, pageable)
                    : closetRepository.findByUserAndIdLessThanOrderByIdDesc(user, cursorId, pageable);
        }

        boolean hasNext = page.size() > size;
        List<Closet> sliced = hasNext ? page.subList(0, size) : page;

        List<ClosetResponseDTO.Summary> summaries = sliced.stream()
                .map(closetConverter::toSummary)
                .toList();

        Long nextCursorId = sliced.isEmpty() ? null : sliced.get(sliced.size() - 1).getId();
        Boolean nextCursorFavorite = sliced.isEmpty() ? null : sliced.get(sliced.size() - 1).isFavorite();

        return new ClosetResponseDTO.CursorListResponse(summaries, nextCursorId, nextCursorFavorite, hasNext);
    }

    @Transactional(readOnly = true)
    public ClosetResponseDTO.Detail getDetail(User user, Long closetId) {
        Closet closet = closetRepository.findByIdAndUser(closetId, user)
                .orElseThrow(() -> new GeneralException(ClosetErrorStatus.CLOSET_NOT_FOUND));

        List<ClosetItem> closetItems = closetItemRepository.findAllWithItemByClosetId(closetId);

        // ✅ 배치정보 없음: DTO 형태(ItemPlacement)로만 내려줌 (closetItemId, itemId)
        List<ClosetResponseDTO.ItemPlacement> items = closetItems.stream()
                .map(ci -> new ClosetResponseDTO.ItemPlacement(
                        ci.getId(),
                        ci.getItem().getId()
                ))
                .toList();

        return new ClosetResponseDTO.Detail(
                closet.getId(),
                closet.getName(),
                closet.getImageUrl(),
                closet.isFavorite(),
                items
        );
    }

    @Transactional(readOnly = true)
    public ClosetResponseDTO.ClosetItemCursorListResponse getClosetItemsByCursor(
            User user,
            Long closetId,
            Long cursorId,
            int size
    ) {
        closetRepository.findByIdAndUser(closetId, user)
                .orElseThrow(() -> new GeneralException(ClosetErrorStatus.CLOSET_NOT_FOUND));

        int limit = size + 1;
        Pageable pageable = PageRequest.of(0, limit);

        List<ClosetItem> page = closetItemRepository.findItemsByClosetIdWithItemByCursor(
                closetId, cursorId, pageable
        );

        boolean hasNext = page.size() > size;
        List<ClosetItem> sliced = hasNext ? page.subList(0, size) : page;

        List<ClosetResponseDTO.ClosetItemCursor> items = sliced.stream()
                .map(ci -> new ClosetResponseDTO.ClosetItemCursor(
                        ci.getId(),
                        ci.getItem().getId()
                ))
                .toList();

        Long nextCursorId = sliced.isEmpty() ? null : sliced.get(sliced.size() - 1).getId();

        return new ClosetResponseDTO.ClosetItemCursorListResponse(items, nextCursorId, hasNext);
    }

    // =========================
    // create() SRP 분리
    // =========================
    private void validateCreateRequest(User user, ClosetRequestDTO.Create request) {
        validateDuplicatedName(user, request.name());
        validateItemsExistAndNoDuplication(request);
    }

    private void validateDuplicatedName(User user, String name) {
        if (closetRepository.existsByUserAndName(user, name)) {
            throw new GeneralException(ClosetErrorStatus.DUPLICATED_NAME);
        }
    }

    private void validateItemsExistAndNoDuplication(ClosetRequestDTO.Create request) {
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
    }

    private Closet saveCloset(User user, ClosetRequestDTO.Create request) {
        Closet closet = closetConverter.toEntity(user, request);

        try {
            return closetRepository.save(closet);
        } catch (DataIntegrityViolationException e) {
            throw new GeneralException(ClosetErrorStatus.DUPLICATED_NAME);
        }
    }

    private void saveClosetItems(Closet saved, ClosetRequestDTO.Create request) {
        List<ClosetItem> closetItems = request.items().stream()
                .map(p -> ClosetItem.builder()
                        .closet(saved)
                        .item(itemRepository.getReferenceById(p.itemId()))
                        .build()
                )
                .toList();

        closetItemRepository.saveAll(closetItems);
    }
}
