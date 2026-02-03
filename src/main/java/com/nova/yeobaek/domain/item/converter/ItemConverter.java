package com.nova.yeobaek.domain.item.converter;

import com.nova.yeobaek.domain.item.domain.Category;
import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.item.domain.Material;
import com.nova.yeobaek.domain.item.domain.enums.Season;
import com.nova.yeobaek.domain.item.dto.request.ItemRequestDTO;
import com.nova.yeobaek.domain.item.dto.response.ItemResponseDTO;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.shared.ImageBackgroundColor;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.mapping.ItemUsage;

import java.util.List;
import java.util.Set;

public class ItemConverter {

    public static Item toItem(
            ItemRequestDTO.CreateItem request,
            User user,
            Category category,
            Material material,
            Set<Season> seasons,
            ImageBackgroundColor imageBackgroundColor
    ) {
        return Item.builder()
                .imageUrl(request.imageUrl())
                .imageBackgroundColor(imageBackgroundColor)
                .category(category)
                .brandName(request.brandName())
                .material(material)
                .seasonSet(seasons)
                .size(request.size())
                .price(request.price())
                .memo(request.memo())
                .user(user)
                .build();
    }

    public static ItemResponseDTO.CreateResponse toCreateResponse(Item item) {
        return new ItemResponseDTO.CreateResponse(item.getId());
    }

    public static ItemResponseDTO.ListItem toListItem(Item item) {
        return new ItemResponseDTO.ListItem(
                item.getId(),
                item.getImageUrl(),
                item.getImageBackgroundColor().name(),
                item.getBrandName()
        );
    }

    public static ItemResponseDTO.ListResponse toListResponse(
            List<Item> items,
            int limit,
            String sort
    ) {
        boolean hasNext = items.size() > limit;
        List<Item> pageItems = hasNext ? items.subList(0, limit) : items;
        Long nextCursor = null;
        String nextCursorBrandname = null;

        if (hasNext && !pageItems.isEmpty()) {
            Item lastItem = pageItems.get(pageItems.size() - 1);
            nextCursor = lastItem.getId();

            if ("NAME_ASC".equals(sort)) {
                nextCursorBrandname = lastItem.getBrandName();
            }
        }

        List<ItemResponseDTO.ListItem> listItems = pageItems.stream()
                .map(ItemConverter::toListItem)
                .toList();

        return new ItemResponseDTO.ListResponse(listItems, nextCursor, nextCursorBrandname, hasNext);
    }

    public static ItemResponseDTO.DetailResponse toDetailResponse(Item item, ItemUsage usage) {
        List<String> colors = item.getItemsColors().stream()
                .map(ic -> ic.getColor().getName())
                .toList();

        List<String> seasons = item.getSeasonSet().stream()
                .map(Season::name)
                .toList();

        return new ItemResponseDTO.DetailResponse(
                item.getId(),
                item.getImageUrl(),
                item.getImageBackgroundColor().name(),
                item.getCategory().getId(),
                item.getCategory().getName(),
                colors,
                seasons,
                item.getBrandName(),
                item.getMaterial() != null ? item.getMaterial().getName() : null,
                item.getSize(),
                item.getPrice(),
                item.getMemo(),
                usage != null ? usage.getUseCount() : 0,
                usage != null ? usage.getLastUsedDate() : null,
                item.getCreatedAt()
        );
    }

    public static ItemResponseDTO.OOTDItem toOOTDItem(OOTD ootd) {
        return new ItemResponseDTO.OOTDItem(
                ootd.getId(),
                ootd.getName(),
                ootd.getImageUrl(),
                ootd.getImageBackgroundColor().name()
        );
    }

    public static ItemResponseDTO.ItemOOTDsResponse toItemOOTDsResponse(List<OOTD> ootds) {
        List<ItemResponseDTO.OOTDItem> ootdItems = ootds.stream()
                .map(ItemConverter::toOOTDItem)
                .toList();

        return new ItemResponseDTO.ItemOOTDsResponse(ootdItems);
    }
}
