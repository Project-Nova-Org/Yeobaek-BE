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
import com.nova.yeobaek.domain.closet.dto.type.ClosetSortType;
import com.nova.yeobaek.domain.closet.dto.response.ClosetResponseDTO;
import com.nova.yeobaek.domain.closet.repository.ClosetRepository;
import com.nova.yeobaek.domain.closet.repository.closetItem.ClosetItemRepository;
import com.nova.yeobaek.domain.closet.status.ClosetErrorStatus;
import com.nova.yeobaek.domain.item.repository.ItemRepository;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.payload.exception.GeneralException;
import com.nova.yeobaek.domain.closet.repository.ClosetEditItemQueryRepository;
import com.nova.yeobaek.domain.closet.repository.ClosetEditItemView;

import java.util.ArrayList;

import com.nova.yeobaek.domain.closet.dto.request.ClosetEditRequestDTO;
import com.nova.yeobaek.domain.closet.dto.response.ClosetEditResponseDTO;
import com.nova.yeobaek.domain.item.domain.Item;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClosetService {

    private final ClosetRepository closetRepository;
    private final ClosetConverter closetConverter;

    private final ItemRepository itemRepository;
    private final ClosetItemRepository closetItemRepository;
    private final ClosetEditItemQueryRepository closetEditItemQueryRepository;

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
            Integer size,
            ClosetSortType sort
    ) {
        int s = normalizeSize(size);
        int limit = s + 1;
        Pageable pageable = PageRequest.of(0, limit);

        if (sort == null) sort = ClosetSortType.LATEST;

        List<Closet> page;

        if (sort == ClosetSortType.FAVORITE_LATEST) {
            if (cursorId == null) {
                page = closetRepository.findByUserOrderByFavoriteDescIdDesc(user, pageable);
            } else {
                boolean effectiveCursorFavorite = (cursorFavorite != null)
                        ? cursorFavorite
                        : resolveCursorFavorite(user, cursorId);

                int cursorFav = effectiveCursorFavorite ? 1 : 0;
                page = closetRepository.findByUserAfterFavoriteDescIdDesc(user, cursorFav, cursorId, pageable);
            }

        } else if (sort == ClosetSortType.FAVORITE_OLDEST) {
            if (cursorId == null) {
                page = closetRepository.findByUserOrderByFavoriteDescIdAsc(user, pageable);
            } else {
                boolean effectiveCursorFavorite = (cursorFavorite != null)
                        ? cursorFavorite
                        : resolveCursorFavorite(user, cursorId);

                int cursorFav = effectiveCursorFavorite ? 1 : 0;
                page = closetRepository.findByUserAfterFavoriteDescIdAsc(user, cursorFav, cursorId, pageable);
            }

        } else if (sort == ClosetSortType.OLDEST) {
            page = (cursorId == null)
                    ? closetRepository.findByUserOrderByIdAsc(user, pageable)
                    : closetRepository.findByUserAndIdGreaterThanOrderByIdAsc(user, cursorId, pageable);

        } else {
            page = (cursorId == null)
                    ? closetRepository.findByUserOrderByIdDesc(user, pageable)
                    : closetRepository.findByUserAndIdLessThanOrderByIdDesc(user, cursorId, pageable);
        }

        boolean hasNext = page.size() > s;
        List<Closet> sliced = hasNext ? page.subList(0, s) : page;

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

        List<ClosetResponseDTO.ClosetItemReference> items = closetItems.stream()
                .map(ci -> new ClosetResponseDTO.ClosetItemReference(
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
            Integer size,
            Long level1CategoryId,
            Long level2CategoryId
    ) {

        Closet closet = closetRepository.findByIdAndUser(closetId, user)
                .orElseThrow(() -> new GeneralException(ClosetErrorStatus.CLOSET_NOT_FOUND));

        int s = normalizeSize(size);
        int limit = s + 1;
        Pageable pageable = PageRequest.of(0, limit);

        List<ClosetItem> page;

        if (level2CategoryId != null) {
            page = closetItemRepository.findItemsByClosetIdWithItemByCursorAndLevel2Category(
                    closetId, cursorId, level2CategoryId, pageable
            );
        } else if (level1CategoryId != null) {
            page = closetItemRepository.findItemsByClosetIdWithItemByCursorAndLevel1Category(
                    closetId, cursorId, level1CategoryId, pageable
            );
        } else {
            page = closetItemRepository.findItemsByClosetIdWithItemByCursor(
                    closetId, cursorId, pageable
            );
        }

        boolean hasNext = page.size() > s;
        List<ClosetItem> sliced = hasNext ? page.subList(0, s) : page;

        List<ClosetResponseDTO.ClosetItemCursor> items = sliced.stream()
                .map(ci -> new ClosetResponseDTO.ClosetItemCursor(ci.getId(), ci.getItem().getId()))
                .toList();

        Long nextCursorId = sliced.isEmpty() ? null : sliced.get(sliced.size() - 1).getId();

        return new ClosetResponseDTO.ClosetItemCursorListResponse(
                closet.getId(),
                closet.getName(),
                closet.getImageUrl(),
                closet.isFavorite(),
                items,
                nextCursorId,
                hasNext
        );
    }

    @Transactional(readOnly = true)
    public ClosetEditResponseDTO.EditInfo getClosetEditInfo(User user, Long closetId) {
        Closet closet = closetRepository.findByIdAndUser(closetId, user)
                .orElseThrow(() -> new GeneralException(ClosetErrorStatus.CLOSET_NOT_FOUND));

        List<Long> selectedItemIds = closetItemRepository.findItemIdsByClosetId(closetId);

        return new ClosetEditResponseDTO.EditInfo(
                closet.getId(),
                closet.getName(),
                closet.getImageUrl(),
                selectedItemIds
        );
    }

    @Transactional(readOnly = true)
    public ClosetEditResponseDTO.EditableItemCursorList getEditableItemsByCursor(
            User user,
            Long closetId,
            Long cursorId,
            Integer size,
            Long level1CategoryId,
            Long level2CategoryId
    ) {
        Closet closet = closetRepository.findByIdAndUser(closetId, user)
                .orElseThrow(() -> new GeneralException(ClosetErrorStatus.CLOSET_NOT_FOUND));

        int s = normalizeSize(size);
        int limit = s + 1;

        List<ClosetEditItemView> rows = closetEditItemQueryRepository.findEditableItemsForClosetEdit(
                user.getId(),
                closet.getId(),
                cursorId,
                level1CategoryId,
                level2CategoryId,
                limit
        );

        boolean hasNext = rows.size() > s;
        if (hasNext) {
            rows = rows.subList(0, s);
        }

        List<ClosetEditResponseDTO.EditableItem> items = rows.stream()
                .map(r -> new ClosetEditResponseDTO.EditableItem(
                        r.getId(),
                        r.getImageUrl(),
                        r.getCategoryId(),
                        Boolean.TRUE.equals(r.getSelectedStatus())
                ))
                .toList();

        Long nextCursorId = rows.isEmpty() ? null : rows.get(rows.size() - 1).getId();

        return new ClosetEditResponseDTO.EditableItemCursorList(items, nextCursorId, hasNext);
    }

    @Transactional
    public Long updateCloset(User user, Long closetId, ClosetEditRequestDTO.Update request) {
        Closet closet = closetRepository.findByIdAndUser(closetId, user)
                .orElseThrow(() -> new GeneralException(ClosetErrorStatus.CLOSET_NOT_FOUND));

        validateUpdateName(user, closet, request.name());
        validateUpdateItems(user, request.itemIds());

        closet.updateInfo(request.name(), request.imageUrl());

        List<Long> existingItemIds = closetItemRepository.findItemIdsByClosetId(closetId);
        Set<Long> existingSet = new HashSet<>(existingItemIds);

        Set<Long> requestedSet = new HashSet<>(request.itemIds());

        List<Long> removeItemIds = existingSet.stream()
                .filter(id -> !requestedSet.contains(id))
                .toList();

        List<Long> addItemIds = requestedSet.stream()
                .filter(id -> !existingSet.contains(id))
                .toList();

        if (!removeItemIds.isEmpty()) {
            closetItemRepository.deleteByCloset_IdAndItem_IdIn(closetId, removeItemIds);
        }

        if (!addItemIds.isEmpty()) {
            List<ClosetItem> toAdd = new ArrayList<>();

            addItemIds.forEach(itemId -> toAdd.add(
                    ClosetItem.builder()
                            .closet(closet)
                            .item(itemRepository.getReferenceById(itemId))
                            .build()
            ));

            try {
                closetItemRepository.saveAll(toAdd);
            } catch (DataIntegrityViolationException e) {
                throw new GeneralException(ClosetErrorStatus.DUPLICATED_ITEM_ID);
            }
        }

        try {
            closetRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new GeneralException(ClosetErrorStatus.DUPLICATED_NAME);
        }

        return closet.getId();
    }

    private int normalizeSize(Integer size) {
        int s = (size == null) ? 20 : size;
        if (s < 1 || s > 100) {
            throw new GeneralException(ClosetErrorStatus.INVALID_SIZE);
        }
        return s;
    }

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

    private void validateUpdateName(User user, Closet closet, String name) {
        if (!closet.getName().equals(name) && closetRepository.existsByUserAndName(user, name)) {
            throw new GeneralException(ClosetErrorStatus.DUPLICATED_NAME);
        }
    }

    private void validateUpdateItems(User user, List<Long> itemIds) {
        Set<Long> set = new HashSet<>();

        itemIds.forEach(id -> {
            if (!set.add(id)) {
                throw new GeneralException(ClosetErrorStatus.DUPLICATED_ITEM_ID);
            }
        });

        List<Item> items = itemRepository.findAllById(itemIds);

        if (items.size() != itemIds.size()) {
            throw new GeneralException(ClosetErrorStatus.ITEM_NOT_FOUND);
        }

        boolean allOwnedByUser = items.stream()
                .allMatch(i -> i.getUser() != null && i.getUser().getId().equals(user.getId()));

        if (!allOwnedByUser) {
            throw new GeneralException(ClosetErrorStatus.ITEM_NOT_FOUND);
        }
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

    private boolean resolveCursorFavorite(User user, Long cursorId) {
        Closet cursorCloset = closetRepository.findByIdAndUser(cursorId, user)
                .orElseThrow(() -> new GeneralException(ClosetErrorStatus.CLOSET_NOT_FOUND));
        return cursorCloset.isFavorite();
    }
}
