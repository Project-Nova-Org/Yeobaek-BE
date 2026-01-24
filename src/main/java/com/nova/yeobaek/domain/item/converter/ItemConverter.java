package com.nova.yeobaek.domain.item.converter;

import com.nova.yeobaek.domain.item.domain.Brand;
import com.nova.yeobaek.domain.item.domain.Category;
import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.item.domain.Material;
import com.nova.yeobaek.domain.item.domain.enums.Season;
import com.nova.yeobaek.domain.item.dto.request.ItemRequestDTO;
import com.nova.yeobaek.domain.item.dto.response.ItemResponseDTO;
import com.nova.yeobaek.domain.shared.ImageBackgroundColor;
import com.nova.yeobaek.domain.user.domain.User;

import java.util.Set;

public class ItemConverter {

    public static Item toItem(
            ItemRequestDTO.Create request,
            User user,
            Category category,
            Brand brand,
            Material material,
            Set<Season> seasons,
            ImageBackgroundColor imageBackgroundColor
    ) {
        return Item.builder()
                .imageUrl(request.imageUrl())
                .imageBackgroundColor(imageBackgroundColor)
                .category(category)
                .brand(brand)
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
}
