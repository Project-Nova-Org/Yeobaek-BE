package com.nova.yeobaek.domain.ootd.converter;

import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.ootd.domain.Style;
import com.nova.yeobaek.domain.ootd.domain.TPO;
import com.nova.yeobaek.domain.ootd.domain.mapping.OOTDItem;
import com.nova.yeobaek.domain.ootd.dto.request.OOTDRequestDTO;
import com.nova.yeobaek.domain.ootd.dto.response.OOTDResponseDTO;
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
            OOTDRequestDTO.CreateOOTD request,
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

    public static OOTDResponseDTO.OOTDListItemResponse toListItem(OOTD ootd) {
        return OOTDResponseDTO.OOTDListItemResponse.builder()
            .ootdId(ootd.getId())
            .name(ootd.getName())
            .tpoId(ootd.getTpo() != null ? ootd.getTpo().getId() : null)
            .styleId(ootd.getStyle() != null ? ootd.getStyle().getId() : null)
            .favorite(ootd.isFavorite())
            .imageBackground(ootd.getImageBackgroundColor().name())
            .coverImageUrl(ootd.getImageUrl())
            .itemNum(ootd.getOotdItemList().size())
            .createdAt(ootd.getCreatedAt())
            .build();
    }

    public static OOTDResponseDTO.OOTDListResponse toListResponse(
        List<OOTD> ootds, int limit, String sort
    ) {
        boolean hasNext = ootds.size() > limit;
        List<OOTD> content = hasNext ? ootds.subList(0, limit) : ootds;

        Long nextCursor = null;
        String nextCursorName = null;

        if (!content.isEmpty()) {
            OOTD lastOotd = content.get(content.size() - 1);
            nextCursor = lastOotd.getId();

            if ("NAME_ASC".equals(sort)) {
                nextCursorName = lastOotd.getName();
            }
        }

        List<OOTDResponseDTO.OOTDListItemResponse> listItems = content.stream()
            .map(OOTDConverter::toListItem)
            .toList();

        return OOTDResponseDTO.OOTDListResponse.builder()
            .items(listItems)
            .nextCursor(nextCursor)
            .nextCursorName(nextCursorName)
            .hasNext(hasNext)
            .build();
    }
}