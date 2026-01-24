package com.nova.yeobaek.domain.item.service;

import com.nova.yeobaek.domain.item.converter.ItemConverter;
import com.nova.yeobaek.domain.item.domain.Brand;
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
import com.nova.yeobaek.domain.item.repository.brand.BrandRepository;
import com.nova.yeobaek.domain.item.repository.category.CategoryRepository;
import com.nova.yeobaek.domain.item.repository.color.ColorRepository;
import com.nova.yeobaek.domain.item.repository.material.MaterialRepository;
import com.nova.yeobaek.domain.item.status.ItemErrorStatus;
import com.nova.yeobaek.domain.shared.ImageBackgroundColor;
import com.nova.yeobaek.domain.user.domain.User;
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
    private final BrandRepository brandRepository;

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

        // 5. 소재 조회 (선택)
        Material material = null;
        if (request.material() != null && !request.material().isBlank()) {
            material = materialRepository.findByName(request.material())
                    .orElseThrow(() -> new ItemException(ItemErrorStatus.INVALID_MATERIAL));
        }

        // 6. 브랜드 조회 또는 생성 (선택)
        Brand brand = null;
        if (request.brandName() != null && !request.brandName().isBlank()) {
            brand = brandRepository.findByName(request.brandName())
                    .orElseGet(() -> brandRepository.save(
                            Brand.builder()
                                    .name(request.brandName())
                                    .build()
                    ));
        }

        // 7. Item 엔티티 생성
        Item item = ItemConverter.toItem(
                request,
                user,
                category,
                brand,
                material,
                seasons,
                imageBackgroundColor
        );

        // 8. Item 저장
        Item savedItem = itemRepository.save(item);

        // 9. ItemsColor 연관관계 설정
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
        List<Color> colors = colorRepository.findByNameIn(colorNames);

        // 요청한 색상 수와 조회된 색상 수가 다르면 유효하지 않은 색상 포함
        if (colors.size() != colorNames.size()) {
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
}
