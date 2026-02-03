package com.nova.yeobaek.domain.ootd.service;

import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.item.dto.request.ItemRequestDTO;
import com.nova.yeobaek.domain.item.repository.ItemRepository;
import com.nova.yeobaek.domain.ootd.converter.OOTDConverter;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.ootd.domain.Style;
import com.nova.yeobaek.domain.ootd.domain.TPO;
import com.nova.yeobaek.domain.ootd.domain.mapping.OOTDItem;
import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;
import com.nova.yeobaek.domain.ootd.repository.OOTDRepository;
import com.nova.yeobaek.domain.ootd.repository.ootdItem.OOTDItemRepository;
import com.nova.yeobaek.domain.ootd.repository.style.StyleRepository;
import com.nova.yeobaek.domain.ootd.repository.tpo.TPORepository;
import com.nova.yeobaek.domain.ootd.status.OOTDErrorStatus;
import com.nova.yeobaek.domain.ootd.status.OOTDException;
import com.nova.yeobaek.domain.shared.ImageBackgroundColor;
import com.nova.yeobaek.domain.user.domain.User;

import com.nova.yeobaek.domain.ootd.dto.response.OOTDListResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nova.yeobaek.domain.ootd.dto.response.OOTDDetailResponse;
import com.nova.yeobaek.domain.ootd.dto.response.OOTDItemDetailResponse;
import com.nova.yeobaek.domain.ootd.domain.enums.OOTDStatus;

import com.nova.yeobaek.domain.calendar.service.CalendarService;
import com.nova.yeobaek.global.payload.exception.GeneralException;
import com.nova.yeobaek.global.payload.status.CommonErrorStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class OOTDService {

    private final CalendarService calendarService;
    private final OOTDRepository ootdRepository;
    private final OOTDItemRepository ootdItemRepository;
    private final StyleRepository styleRepository;
    private final TPORepository tpoRepository;
    private final ItemRepository itemRepository;

    /** OOTD 등록 */
    @Transactional
    public Long createOOTD(User user, OOTDRequestDTO.CreateOOTD requestDTO) {
        Style style = findStyle(requestDTO.styleId());
        TPO tpo = findTpo(requestDTO.tpoId());
        ImageBackgroundColor backgroundColor = parseBackground(requestDTO.imageBackground());
        List<Long> itemIds = extractItemIds(requestDTO);
        validateDuplicatedItemIds(itemIds);
        Map<Long, Item> itemMap = loadItemsOrThrow(itemIds);
        OOTD ootd = createAndSaveOOTD(requestDTO, user, style, tpo, backgroundColor);
        saveOOTDItems(requestDTO, ootd, itemMap);
        return ootd.getId();
    }

    private Style findStyle(Long styleId) {
        return styleRepository.findById(styleId)
                .orElseThrow(() -> new OOTDException(OOTDErrorStatus.STYLE_NOT_FOUND));
    }

    private TPO findTpo(Long tpoId) {
        return tpoRepository.findById(tpoId)
                .orElseThrow(() -> new OOTDException(OOTDErrorStatus.TPO_NOT_FOUND));
    }

    private ImageBackgroundColor parseBackground(String value) {
        try {
            return ImageBackgroundColor.from(value);
        } catch (IllegalArgumentException e) {
            throw new OOTDException(OOTDErrorStatus.INVALID_IMAGE_BACKGROUND);
        }
    }

    private List<Long> extractItemIds(OOTDRequestDTO.CreateOOTD requestDTO) {
        return requestDTO.items().stream()
                .map(OOTDRequestDTO.Item::fashionItemId)
                .toList();
    }

    private void validateDuplicatedItemIds(List<Long> itemIds) {
        if (itemIds.size() != itemIds.stream().distinct().count()) {
            throw new OOTDException(OOTDErrorStatus.DUPLICATED_ITEM_ID);
        }
    }

    private Map<Long, Item> loadItemsOrThrow(List<Long> itemIds) {
        List<Item> items = itemRepository.findAllById(itemIds);
        if (items.size() != itemIds.size()) {
            throw new OOTDException(OOTDErrorStatus.ITEM_NOT_FOUND);
        }
        return items.stream()
                .collect(Collectors.toMap(Item::getId, item -> item));
    }

    private OOTD createAndSaveOOTD(OOTDRequestDTO.CreateOOTD requestDTO, User user, Style style, TPO tpo, ImageBackgroundColor bg) {
        OOTD ootd = OOTDConverter.toOOTD(
                requestDTO,
                user,
                style,
                tpo,
                bg
        );
        ootdRepository.save(ootd);
        return ootd;
    }

    private void saveOOTDItems(OOTDRequestDTO.CreateOOTD requestDTO, OOTD ootd, Map<Long, Item> itemMap) {
        List<OOTDItem> ootdItems = OOTDConverter.toOOTDItems(
                requestDTO.items(),
                ootd,
                itemMap
        );
        ootdItemRepository.saveAll(ootdItems);
    }

    /** OOTD 즐겨찾기 */
    @Transactional
    public void toggleFavorite(User user, Long ootdId) {
        OOTD ootd = getAuthorizedOOTD(user, ootdId);
        ootd.toggleFavorite();
    }

    /** OOTD 수정 */
    @Transactional
    //OOTD 수정 유스케이스
    public void updateOOTD(
            User user,
            Long ootdId,
            OOTDRequestDTO.UpdateOOTD requestDTO
    ) {
        OOTD ootd = getAuthorizedOOTD(user, ootdId);

        updateBasicFields(ootd, requestDTO);
        updateRelations(ootd, requestDTO);
        updateImageBackgroundIfNeeded(ootd, requestDTO);
        updateItemsIfNeeded(ootd, requestDTO);
    }

    //수정,삭제 OOTD 조회 및 권한 검증
    private OOTD getAuthorizedOOTD(User user, Long ootdId) {
        return ootdRepository.findById(ootdId)
                .filter(o -> o.getStatus() == OOTDStatus.NORMAL)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new OOTDException(OOTDErrorStatus.OOTD_NOT_FOUND));
    }

    //OOTD 기본 필드 수정
    private void updateBasicFields(OOTD ootd, OOTDRequestDTO.UpdateOOTD requestDTO) {
        if (requestDTO.name() != null) {
            ootd.updateName(requestDTO.name());
        }
        if (requestDTO.memo() != null) {
            ootd.updateMemo(requestDTO.memo());
        }
        if (requestDTO.favorite() != null) {
            ootd.updateFavorite(requestDTO.favorite());
        }
    }

    //OOTD 연관 엔티티 수정
    private void updateRelations(OOTD ootd, OOTDRequestDTO.UpdateOOTD requestDTO) {
        if (requestDTO.tpoId() != null) {
            TPO tpo = findTpo(requestDTO.tpoId());
            ootd.updateTpo(tpo);
        }
        if (requestDTO.styleId() != null) {
            Style style = findStyle(requestDTO.styleId());
            ootd.updateStyle(style);
        }
    }

    //OOTD 이미지 배경색상 수정
    private void updateImageBackgroundIfNeeded(OOTD ootd, OOTDRequestDTO.UpdateOOTD requestDTO) {
        if (requestDTO.imageBackground() != null) {
            ImageBackgroundColor backgroundColor =
                    parseBackground(requestDTO.imageBackground());
            ootd.updateImageBackground(backgroundColor);
        }
    }

    //OOTD 아이템 구성 수정
    private void updateItemsIfNeeded(OOTD ootd, OOTDRequestDTO.UpdateOOTD requestDTO) {
        if (requestDTO.items() == null) {
            return;
        }

        // 1. before 아이템 ID 목록
        List<Long> beforeItemIds = ootd.getOotdItemList().stream()
                .map(oi -> oi.getItem().getId())
                .toList();

        // 2. after 아이템 ID 목록
        List<Long> afterItemIds = requestDTO.items().stream()
                .map(OOTDRequestDTO.Item::fashionItemId)
                .toList();

        validateDuplicatedItemIds(afterItemIds);

        // 3. diff 계산
        List<Long> removedItemIds = beforeItemIds.stream()
                .filter(id -> !afterItemIds.contains(id))
                .toList();

        List<Long> addedItemIds = afterItemIds.stream()
                .filter(id -> !beforeItemIds.contains(id))
                .toList();

        // 4. 기존 매핑 삭제
        ootdItemRepository.deleteAllByOotd_Id(ootd.getId());
        ootd.clearOotdItems();

        // 5. 새 매핑 저장
        Map<Long, Item> itemMap = loadItemsOrThrow(afterItemIds);
        List<OOTDItem> newItems =
                OOTDConverter.toOOTDItems(requestDTO.items(), ootd, itemMap);
        ootdItemRepository.saveAll(newItems);

        ootd.increaseChangeItemCount();

        // 6. 캘린더 기준 아이템 사용횟수 diff 반영
        calendarService.updateItemUsageByOotdChange(
                ootd.getUser(),
                ootd.getId(),
                removedItemIds,
                addedItemIds
        );
    }

    /** OOTD 삭제 (하드 딜리트) */
    @Transactional
    public void deleteOOTD(User user, Long ootdId) {

        OOTD ootd = ootdRepository.findById(ootdId)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new OOTDException(OOTDErrorStatus.OOTD_NOT_FOUND));

        // 캘린더 도메인에 위임 (아이템 사용 횟수 감소도 여기서 처리)
        calendarService.deleteAllEntriesByOotd(user, ootdId);

        // OOTD-Item 매핑 삭제
        ootdItemRepository.deleteAllByOotd_Id(ootdId);

        // OOTD 하드 딜리트
        ootdRepository.delete(ootd);
    }

    /** OOTD 목록조회 */
    @Transactional(readOnly = true)
    public OOTDListResponse getOOTDList(
            User user, OOTDRequestDTO.OOTDSearchCondition condition
    ) {

        if (condition.resolvedSort() == ItemRequestDTO.ItemSearchCondition.SortType.NAME_ASC) {
            boolean hasCursor = condition.cursor() != null;
            boolean hasCursorBrand = condition.cursorName() != null && !condition.cursorName().isBlank();

            if (hasCursor != hasCursorBrand) {
                throw new GeneralException(CommonErrorStatus.INVALID_CURSOR);
            }
        }

        List<OOTD> ootds = ootdRepository.findOOTDList(
            user,
            condition.keyword(),
            condition.favorite(),
            condition.resolvedTpoIds(),
            condition.resolvedStyleIds(),
            condition.resolvedSort().name(),
            condition.cursor(),
            condition.cursorName(),
            condition.resolvedLimit()
        );

        return OOTDConverter.toListResponse(ootds, condition.resolvedLimit(), condition.resolvedSort().name());
    }

    /** OOTD 상세조회 */
    @Transactional(readOnly = true)
    public OOTDDetailResponse getOOTDDetail(User user, Long ootdId) {

        OOTD ootd = ootdRepository.findById(ootdId)
                .filter(o -> o.getStatus() == OOTDStatus.NORMAL)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new OOTDException(OOTDErrorStatus.OOTD_NOT_FOUND));

        return OOTDDetailResponse.builder()
                .ootdId(ootd.getId())
                .name(ootd.getName())
                .memo(ootd.getMemo())
                .favorite(ootd.isFavorite())
                .imageBackground(ootd.getImageBackgroundColor().name())
                .imageUrl(ootd.getImageUrl())
                .tpoId(ootd.getTpo() != null ? ootd.getTpo().getId() : null)
                .styleId(ootd.getStyle() != null ? ootd.getStyle().getId() : null)
                .createdAt(ootd.getCreatedAt())
                .items(
                        ootd.getOotdItemList().stream()
                                .map(oi -> OOTDItemDetailResponse.builder()
                                        .fashionItemId(oi.getItem().getId())
                                        .posX(oi.getPosX())
                                        .posY(oi.getPosY())
                                        .scale(oi.getScale())
                                        .rotation(oi.getRotation())
                                        .zIndex(oi.getZIndex())
                                        .build()
                                )
                                .toList()
                )
                .build();
    }
}