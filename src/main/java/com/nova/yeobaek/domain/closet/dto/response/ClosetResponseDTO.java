package com.nova.yeobaek.domain.closet.dto.response;

import java.util.List;

public class ClosetResponseDTO {

    public record Create(Long closetId) {}

    public record Summary(
            Long closetId,
            String name,
            String imageUrl,
            boolean favorite
    ) {}

    public record ListResponse(
            List<Summary> closets,
            Long nextCursorId,
            boolean hasNext
    ) {}


    public record ItemPlacement(
            Long closetItemId,
            Long itemId
    ) {}

    public record FavoriteUpdate(
            Long closetId,
            boolean favorite
    ) {}

    public record CursorListResponse(
            List<Summary> closets,
            Long nextCursorId,
            Boolean nextCursorFavorite,
            boolean hasNext
    ) {}

    public record Detail(
            Long closetId,
            String name,
            String imageUrl,
            boolean favorite,
            List<ItemPlacement> items
    ) {}

    // ✅ /closets/{closetId}/items 커서 응답도 동일하게(배치정보 없음)
    public record ClosetItemCursor(
            Long closetItemId,
            Long itemId
    ) {}

    public record ClosetItemCursorListResponse(
            List<ClosetItemCursor> items,
            Long nextCursorId,
            boolean hasNext
    ) {}
}
