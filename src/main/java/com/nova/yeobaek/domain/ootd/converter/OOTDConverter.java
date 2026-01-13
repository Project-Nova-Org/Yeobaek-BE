package com.nova.yeobaek.domain.ootd.converter;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.ootd.domain.Style;
import com.nova.yeobaek.domain.ootd.domain.TPO;
import com.nova.yeobaek.domain.shared.ImageBackgroundColor;
import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.ootd.domain.mapping.OOTDItem;
import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OOTDConverter {

    public static OOTD toOOTD(
            OOTDRequestDTO request,
            User user,
            Style style,
            TPO tpo,
            ImageBackgroundColor backgroundColor
    ) {
        return OOTD.builder()
                .user(user)
                .style(style)
                .tpo(tpo)
                .name(request.getName())
                .memo(request.getMemo())
                .imageBackgroundColor(backgroundColor)
                .imageUrl("")
                .build();
    }

    /** OOTDItem 리스트 생성 */
    public static List<OOTDItem> toOOTDItems(
            List<OOTDRequestDTO.OOTDItemRequestDTO> items,
            OOTD ootd,
            Map<Long, Item> itemMap
    ) {
        return items.stream()
                .map(itemDTO -> {

                    Item item = itemMap.get(itemDTO.getFashionItemId());

                    return OOTDItem.builder()
                            .ootd(ootd)
                            .item(item) // 영속 상태 Item
                            .posX(itemDTO.getPosX())
                            .posY(itemDTO.getPosY())
                            .scale(itemDTO.getScale())
                            .rotation(itemDTO.getRotation())
                            .zIndex(itemDTO.getZIndex().intValue())
                            .build();
                })
                .collect(Collectors.toList());
    }
}