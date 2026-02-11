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

    // (closetItemId, itemId) 참조쌍
    public record ClosetItemReference(
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
            List<ClosetItemReference> items
    ) {}

    // /closets/{closetId}/items 커서 응답 아이템 1개 단위
    public record ClosetItemCursor(
            Long closetItemId,
            Long itemId
    ) {}


    public record ClosetItemCursorListResponse(
            Long closetId,
            String closetName,
            String imageUrl,
            boolean favorite,
            List<ClosetItemCursor> items,
            Long nextCursorId,
            boolean hasNext
    ) {}

    public record DeleteResult(Long closetId) {}
}
