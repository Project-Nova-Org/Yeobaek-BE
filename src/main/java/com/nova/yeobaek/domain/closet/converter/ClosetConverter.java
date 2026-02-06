package com.nova.yeobaek.domain.closet.converter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nova.yeobaek.domain.closet.domain.Closet;
import com.nova.yeobaek.domain.closet.domain.mapping.ClosetItem;
import com.nova.yeobaek.domain.closet.dto.request.ClosetRequestDTO;
import com.nova.yeobaek.domain.closet.dto.response.ClosetResponseDTO;
import com.nova.yeobaek.domain.user.domain.User;

@Component
public class ClosetConverter {

    public Closet toEntity(User user, ClosetRequestDTO.Create request) {
        return Closet.builder()
                .user(user)
                .name(request.name())
                .imageUrl(request.imageUrl())
                .favorite(false)
                .build();
    }

    public ClosetResponseDTO.Create toCreateResponse(Closet closet) {
        return new ClosetResponseDTO.Create(closet.getId());
    }

    public ClosetResponseDTO.Summary toSummary(Closet closet) {
        return new ClosetResponseDTO.Summary(
                closet.getId(),
                closet.getName(),
                closet.getImageUrl(),
                closet.isFavorite()
        );
    }

    public ClosetResponseDTO.Detail toDetail(Closet closet, List<ClosetItem> closetItems) {

        List<ClosetResponseDTO.ClosetItemReference> items = closetItems.stream()
                .map(ci -> new ClosetResponseDTO.ClosetItemReference(
                        ci.getId(),
                        ci.getItem().getId()
                ))
                .toList();

        return new ClosetResponseDTO.Detail(
                closet.getId(),
                closet.getName(),
                closet.getImageUrl(),
                closet.isFavorite(),
                items
        );
    }

}
