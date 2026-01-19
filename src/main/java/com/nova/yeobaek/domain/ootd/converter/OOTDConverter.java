package com.nova.yeobaek.domain.ootd.converter;

import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.ootd.domain.Style;
import com.nova.yeobaek.domain.ootd.domain.TPO;
import com.nova.yeobaek.domain.ootd.domain.mapping.OOTDItem;
import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;
import com.nova.yeobaek.domain.shared.ImageBackgroundColor;
import com.nova.yeobaek.domain.user.domain.User;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OOTDConverter {

    /** OOTD 엔티티 생성 */
    public static OOTD toOOTD(
            OOTDRequestDTO.Create request,
            User user,
            Style style,
            TPO tpo,
            ImageBackgroundColor backgroundColor
    ) {
        return OOTD.builder()
                .user(user)
                .style(style)
                .tpo(tpo)
                .name(request.name())
                .memo(request.memo())
                .imageBackgroundColor(backgroundColor)
                .imageUrl("") // TODO: 이미지 업로드 구현 시 수정
                .build();
    }

    /** OOTDItem 리스트 생성 */
    public static List<OOTDItem> toOOTDItems(
            List<OOTDRequestDTO.Item> items,
            OOTD ootd,
            Map<Long, Item> itemMap
    ) {
        return items.stream()
                .map(itemDTO -> {

                    Item item = itemMap.get(itemDTO.fashionItemId());

                    return OOTDItem.builder()
                            .ootd(ootd)
                            .item(item) // 영속 상태 Item
                            .posX(itemDTO.posX())
                            .posY(itemDTO.posY())
                            .scale(itemDTO.scale())
                            .rotation(itemDTO.rotation())
                            .zIndex(itemDTO.zIndex().intValue())
                            .build();
                })
                .collect(Collectors.toList());
    }
}