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
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OOTDConverter {

    /** OOTD 엔티티 생성 */
    public static OOTD toOOTD(
            OOTDRequestDTO request,
            User user,
            Style style,
            TPO tpo
    ) {
        return OOTD.builder()
                .user(user)
                .style(style)
                .tpo(tpo)
                .name(request.getName())
                .memo(request.getMemo())
                .imageBackgroundColor(
                        ImageBackgroundColor.valueOf(request.getImageBackground())
                )
                // 현재 imageUrl 필수 컬럼 → 임시 빈 값
                .imageUrl("")
                .build();
    }

    /** OOTDItem 리스트 생성 */
    public static List<OOTDItem> toOOTDItems(
            List<OOTDRequestDTO.OOTDItemRequestDTO> items,
            OOTD ootd
    ) {
        return items.stream()
                .map(itemDTO -> {

                    Item item = Item.builder()
                            .id(itemDTO.getFashionItemId())
                            .build();

                    return OOTDItem.builder()
                            .ootd(ootd)
                            .item(item)
                            .posX(itemDTO.getPosX())
                            .posY(itemDTO.getPosY())
                            .scale(itemDTO.getScale())
                            .rotation(itemDTO.getRotation())
                            .zIndex(
                                    Objects.requireNonNull(
                                            itemDTO.getZIndex(),
                                            "zIndex는 필수입니다."
                                    ).intValue()
                            )
                            .build();
                })
                .collect(Collectors.toList());
    }
}