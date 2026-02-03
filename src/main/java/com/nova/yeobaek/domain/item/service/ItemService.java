package com.nova.yeobaek.domain.item.service;

import com.nova.yeobaek.domain.item.converter.ItemConverter;
import com.nova.yeobaek.domain.item.domain.Category;
import com.nova.yeobaek.domain.item.domain.Color;
import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.item.domain.Material;
import com.nova.yeobaek.domain.item.domain.enums.Season;
import com.nova.yeobaek.domain.item.domain.mapping.ItemsColor;
import com.nova.yeobaek.domain.item.dto.request.ItemRequestDTO;
import com.nova.yeobaek.domain.item.dto.response.ItemResponseDTO;
import com.nova.yeobaek.domain.item.exception.ItemException;
import com.nova.yeobaek.domain.item.repository.ItemRepository;
import com.nova.yeobaek.domain.item.repository.category.CategoryRepository;
import com.nova.yeobaek.domain.item.repository.color.ColorRepository;
import com.nova.yeobaek.domain.item.repository.material.MaterialRepository;
import com.nova.yeobaek.domain.item.status.ItemErrorStatus;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.ootd.domain.enums.OOTDStatus;
import com.nova.yeobaek.domain.ootd.repository.OOTDRepository;
import com.nova.yeobaek.domain.shared.ImageBackgroundColor;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.mapping.ItemUsage;
import com.nova.yeobaek.domain.user.repository.itemUsage.ItemUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ColorRepository colorRepository;
    private final MaterialRepository materialRepository;
    private final OOTDRepository ootdRepository;
    private final ItemUsageRepository itemUsageRepository;

    public ItemResponseDTO.CreateResponse createItem(User user, ItemRequestDTO.Create request) {
        // 1. 이미지 배경 색상 검증
        ImageBackgroundColor imageBackgroundColor = parseImageBackground(request.imageBackground());

        // 2. 카테고리 조회
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ItemException(ItemErrorStatus.CATEGORY_NOT_FOUND));

        // 3. 색상 조회 및 검증
        List<Color> colors = findAndValidateColors(request.colors());

        // 4. 계절 검증
        Set<Season> seasons = parseAndValidateSeasons(request.seasons());

        // 5. 소재 조회 (필수)
        Material material = materialRepository.findByName(request.material())
                .orElseThrow(() -> new ItemException(ItemErrorStatus.INVALID_MATERIAL));

        // 6. Item 엔티티 생성
        Item item = ItemConverter.toItem(
                request,
                user,
                category,
                material,
                seasons,
                imageBackgroundColor
        );

        // 7. Item 저장
        Item savedItem = itemRepository.save(item);

        // 8. ItemsColor 연관관계 설정
        for (Color color : colors) {
            ItemsColor itemsColor = ItemsColor.builder()
                    .item(savedItem)
                    .color(color)
                    .build();
            savedItem.getItemsColors().add(itemsColor);
        }

        return ItemConverter.toCreateResponse(savedItem);
    }

    private ImageBackgroundColor parseImageBackground(String imageBackground) {
        try {
            return ImageBackgroundColor.valueOf(imageBackground.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ItemException(ItemErrorStatus.INVALID_IMAGE_BACKGROUND);
        }
    }

    private List<Color> findAndValidateColors(List<String> colorNames) {
        // 중복 색상 제거
        List<String> distinctColorNames = colorNames.stream().distinct().toList();

        List<Color> colors = colorRepository.findByNameIn(distinctColorNames);

        // 요청한 색상 수와 조회된 색상 수가 다르면 유효하지 않은 색상 포함
        if (colors.size() != distinctColorNames.size()) {
            throw new ItemException(ItemErrorStatus.INVALID_COLOR);
        }

        return colors;
    }

    private Set<Season> parseAndValidateSeasons(List<String> seasonNames) {
        Set<Season> seasons = new HashSet<>();

        for (String seasonName : seasonNames) {
            try {
                seasons.add(Season.valueOf(seasonName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new ItemException(ItemErrorStatus.INVALID_SEASON);
            }
        }

        return seasons;
    }

    public void updateItem(User user, Long itemId, ItemRequestDTO.Update request) {
        // 1. 본인 아이템 조회
        Item item = itemRepository.findByIdAndUser(itemId, user)
                .orElseThrow(() -> new ItemException(ItemErrorStatus.ITEM_NOT_FOUND));

        // 2. 이미지 URL 수정
        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            item.updateImageUrl(request.imageUrl());
        }

        // 3. 이미지 배경 색상 수정
        if (request.imageBackground() != null && !request.imageBackground().isBlank()) {
            ImageBackgroundColor imageBackgroundColor = parseImageBackground(request.imageBackground());
            item.updateImageBackgroundColor(imageBackgroundColor);
        }

        // 4. 카테고리 수정
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ItemException(ItemErrorStatus.CATEGORY_NOT_FOUND));
            item.updateCategory(category);
        }

        // 5. 색상 수정 (전체 교체)
        if (request.colors() != null && !request.colors().isEmpty()) {
            List<Color> colors = findAndValidateColors(request.colors());
            item.clearColors();
            for (Color color : colors) {
                ItemsColor itemsColor = ItemsColor.builder()
                        .item(item)
                        .color(color)
                        .build();
                item.getItemsColors().add(itemsColor);
            }
        }

        // 6. 계절 수정
        if (request.seasons() != null && !request.seasons().isEmpty()) {
            Set<Season> seasons = parseAndValidateSeasons(request.seasons());
            item.updateSeasons(seasons);
        }

        // 7. 브랜드명 수정
        if (request.brandName() != null) {
            item.updateBrandName(request.brandName().isBlank() ? null : request.brandName());
        }

        // 8. 소재 수정
        if (request.material() != null) {
            if (request.material().isBlank()) {
                item.updateMaterial(null);
            } else {
                Material material = materialRepository.findByName(request.material())
                        .orElseThrow(() -> new ItemException(ItemErrorStatus.INVALID_MATERIAL));
                item.updateMaterial(material);
            }
        }

        // 9. 사이즈 수정
        if (request.size() != null) {
            item.updateSize(request.size().isBlank() ? null : request.size());
        }

        // 10. 가격 수정
        if (request.price() != null) {
            item.updatePrice(request.price());
        }

        // 11. 메모 수정
        if (request.memo() != null) {
            item.updateMemo(request.memo().isBlank() ? null : request.memo());
        }
    }

    public void deleteItem(User user, Long itemId) {
        // 1. 본인 아이템 조회
        Item item = itemRepository.findByIdAndUser(itemId, user)
                .orElseThrow(() -> new ItemException(ItemErrorStatus.ITEM_NOT_FOUND));

        // 2. 해당 아이템이 포함된 OOTD들의 status를 ABNORMAL로 변경
        ootdRepository.updateStatusByItemId(itemId, OOTDStatus.ABNORMAL);

        // 3. 연관 데이터 벌크 삭제
        itemRepository.bulkDeleteOOTDItemsByItemId(itemId);
        itemRepository.bulkDeleteClosetItemsByItemId(itemId);
        itemRepository.bulkDeleteItemUsagesByItemId(itemId);
        itemRepository.bulkDeleteItemsColorsByItemId(itemId);
        itemRepository.bulkDeleteSeasonsByItemId(itemId);

        // 4. 아이템 벌크 삭제
        itemRepository.bulkDeleteById(itemId);
    }

    @Transactional(readOnly = true)
    public ItemResponseDTO.ListResponse getItemList(User user, ItemRequestDTO.SearchCondition condition) {
        List<Item> items = itemRepository.findItemList(
                user,
                condition.categoryId(),
                condition.season().name(),
                condition.material(),
                condition.keyword(),
                condition.resolvedSort().name(),
                condition.cursor(),
                condition.resolvedLimit()
        );

        return ItemConverter.toListResponse(items, condition.resolvedLimit());
    }

    @Transactional(readOnly = true)
    public ItemResponseDTO.DetailResponse getItemDetail(User user, Long itemId) {
        Item item = itemRepository.findByIdAndUser(itemId, user)
                .orElseThrow(() -> new ItemException(ItemErrorStatus.ITEM_NOT_FOUND));

        ItemUsage usage = itemUsageRepository.findByUser_IdAndItem_Id(user.getId(), itemId)
                .orElse(null);

        return ItemConverter.toDetailResponse(item, usage);
    }

    @Transactional(readOnly = true)
    public ItemResponseDTO.ItemOOTDsResponse getItemOOTDs(User user, Long itemId) {
        // 본인 아이템인지 확인
        itemRepository.findByIdAndUser(itemId, user)
                .orElseThrow(() -> new ItemException(ItemErrorStatus.ITEM_NOT_FOUND));

        // 해당 아이템이 포함된 OOTD 목록 조회
        List<OOTD> ootds = ootdRepository.findAllByItemIdAndUserId(itemId, user.getId());

        return ItemConverter.toItemOOTDsResponse(ootds);
    }
}
