package com.nova.yeobaek.domain.closet.dto.response;

import java.util.List;

public class ClosetEditResponseDTO {

    public record EditInfo(
            Long closetId,
            String name,
            String imageUrl,
            List<Long> selectedItemIds
    ) {
    }

    public record EditableItem(
            Long itemId,
            String imageUrl,
            Long categoryId,
            boolean selectedStatus
    ) {
    }

    public record EditableItemCursorList(
            List<EditableItem> items,
            Long nextCursorId,
            boolean hasNext
    ) {
    }

    public record UpdateResult(
            Long closetId
    ) {
    }
}
