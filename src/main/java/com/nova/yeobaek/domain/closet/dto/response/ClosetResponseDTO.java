package com.nova.yeobaek.domain.closet.dto.response;

import java.util.List;

public class ClosetResponseDTO {

    public record Create(Long closetId) {}

    // === added ===
    public record Summary(
            Long closetId,
            String name,
            String imageUrl,
            boolean favorite
    ) {}

    // === added ===
    public record ListResponse(
            List<Summary> closets
    ) {}

    // === added ===
    public record ItemPlacement(
            Long itemId,
            Double posX,
            Double posY,
            Double scale,
            Double rotation,
            Integer zIndex
    ) {}

    // === added ===
    public record Detail(
            Long closetId,
            String name,
            String imageUrl,
            boolean favorite,
            List<ItemPlacement> items
    ) {}
}
