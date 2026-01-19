package com.nova.yeobaek.domain.ootd.service;

import com.nova.yeobaek.domain.item.domain.Item;
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
import com.nova.yeobaek.domain.ootd.dto.response.OOTDListItemResponse;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class OOTDService {

    private final OOTDRepository ootdRepository;
    private final OOTDItemRepository ootdItemRepository;
    private final StyleRepository styleRepository;
    private final TPORepository tpoRepository;
    private final ItemRepository itemRepository;

    /** OOTD 등록 */
    @Transactional
    public Long createOOTD(User user, OOTDRequestDTO.Create requestDTO) {
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

    private List<Long> extractItemIds(OOTDRequestDTO.Create requestDTO) {
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

    private OOTD createAndSaveOOTD(OOTDRequestDTO.Create requestDTO, User user, Style style, TPO tpo, ImageBackgroundColor bg) {
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

    private void saveOOTDItems(OOTDRequestDTO.Create requestDTO, OOTD ootd, Map<Long, Item> itemMap) {
        List<OOTDItem> ootdItems = OOTDConverter.toOOTDItems(
                requestDTO.items(),
                ootd,
                itemMap
        );
        ootdItemRepository.saveAll(ootdItems);
    }

    /** OOTD 수정 */
    @Transactional
    public void updateOOTD(
            User user,
            Long ootdId,
            OOTDRequestDTO.Update requestDTO
    ) {
        // 1. OOTD 조회, 권한 검증
        OOTD ootd = ootdRepository.findById(ootdId)
                .filter(o -> o.getStatus() == OOTDStatus.NORMAL)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new OOTDException(OOTDErrorStatus.OOTD_NOT_FOUND));

        // 2. 기본 필드 수정 (null이면 스킵)
        if (requestDTO.name() != null) {
            ootd.updateName(requestDTO.name());
        }

        if (requestDTO.memo() != null) {
            ootd.updateMemo(requestDTO.memo());
        }

        if (requestDTO.favorite() != null) {
            ootd.updateFavorite(requestDTO.favorite());
        }

        // 3. TPO / Style 수정
        if (requestDTO.tpoId() != null) {
            TPO tpo = findTpo(requestDTO.tpoId());
            ootd.updateTpo(tpo);
        }
        if (requestDTO.styleId() != null) {
            Style style = findStyle(requestDTO.styleId());
            ootd.updateStyle(style);
        }

        // 4. 이미지 배경 색상 수정
        if (requestDTO.imageBackground() != null) {
            ImageBackgroundColor backgroundColor =
                    parseBackground(requestDTO.imageBackground());
            ootd.updateImageBackground(backgroundColor);
        }

        // 5. 아이템 수정 (전체교체방식)
        if (requestDTO.items() != null) {

            // 기존 아이템 스냅샷 (캘린더/착용횟수 diff 계산용 - TODO:추후 사용)
            List<OOTDItem> beforeItems = ootd.getOotdItemList();

            // 기존 아이템 매핑 삭제
            ootdItemRepository.deleteAllByOotd_Id(ootd.getId());

            // 아이템 검증
            List<Long> itemIds = requestDTO.items().stream()
                    .map(OOTDRequestDTO.Item::fashionItemId)
                    .toList();

            validateDuplicatedItemIds(itemIds);
            Map<Long, Item> itemMap = loadItemsOrThrow(itemIds);

            // 새 아이템 저장
            List<OOTDItem> newItems =
                    OOTDConverter.toOOTDItems(requestDTO.items(), ootd, itemMap);

            ootdItemRepository.saveAll(newItems);

            // 아이템 변경 횟수 카운트
            ootd.increaseChangeItemCount();

            // TODO(#calendar): 해당 OOTD가 기록된 캘린더 엔트리들의 ootdImageUrl 최신화
            // TODO(#item): 캘린더 기록 수 기준으로 아이템 착용 횟수 diff 반영
            // beforeItems 와 newItems 비교하여 계산 예정
        }
    }

    /** OOTD 삭제 (하드 딜리트) */
    @Transactional
    public void deleteOOTD(User user, Long ootdId) {

        OOTD ootd = ootdRepository.findById(ootdId)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new OOTDException(OOTDErrorStatus.OOTD_NOT_FOUND));

        // 1. OOTD에 연결된 아이템 매핑 삭제
        ootdItemRepository.deleteAllByOotd_Id(ootd.getId());

        // 2. TODO: 캘린더 파트 머지 후 연동
        // TODO(#calendar): 해당 OOTD가 기록된 모든 캘린더 엔트리 삭제
        // TODO(#calendar): 캘린더 삭제에 따른 아이템 사용 횟수 차감
        // 아직 캘린더 도메인이 푸쉬되지 않아서 제가 건드릴 수 없어서 일단 투두로 뒀습니다!!

        // 3. OOTD 하드딜리트
        ootdRepository.delete(ootd);
    }

    /** OOTD 목록조회 */
    @Transactional(readOnly = true)
    public OOTDListResponse getOOTDList(
            User user,
            OOTDRequestDTO.SearchCondition condition
    ) {
        return getOOTDList(
                user,
                condition.keyword(),
                condition.favorite(),
                condition.resolvedTpoIds(),
                condition.resolvedStyleIds(),
                condition.resolvedSort(),
                condition.cursor(),
                condition.resolvedLimit()
        );
    }

    /** OOTD 목록조회 */
    @Transactional(readOnly = true)
    public OOTDListResponse getOOTDList(
            User user,
            String keyword,
            Boolean favorite,
            List<Long> tpoIds,
            List<Long> styleIds,
            String sort,
            Long cursor,
            int limit
    ) {
        List<OOTD> ootds = ootdRepository.findOOTDList(
                user,
                keyword,
                favorite,
                tpoIds,
                styleIds,
                sort,
                cursor,
                limit + 1
        );

        boolean hasNext = ootds.size() > limit;
        if (hasNext) {
            ootds = ootds.subList(0, limit);
        }

        List<OOTDListItemResponse> items = ootds.stream()
                .map(ootd -> OOTDListItemResponse.builder()
                        .ootdId(ootd.getId())
                        .name(ootd.getName())
                        .tpoId(ootd.getTpo() != null ? ootd.getTpo().getId() : null)
                        .styleId(ootd.getStyle() != null ? ootd.getStyle().getId() : null)
                        .favorite(ootd.isFavorite())
                        .imageBackground(ootd.getImageBackgroundColor().name())
                        .coverImageUrl(ootd.getImageUrl())
                        .itemNum(ootd.getOotdItemList().size())
                        .createdAt(ootd.getCreatedAt())
                        .build()
                )
                .toList();

        Long nextCursor = hasNext ? items.get(items.size() - 1).getOotdId() : null;

        return OOTDListResponse.builder()
                .items(items)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
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