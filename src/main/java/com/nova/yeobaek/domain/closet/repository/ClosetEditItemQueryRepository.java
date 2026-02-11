package com.nova.yeobaek.domain.closet.repository;

import java.util.List;

public interface ClosetEditItemQueryRepository {
    List<ClosetEditItemView> findEditableItemsForClosetEdit(
            Long userId,
            Long closetId,
            Long cursorId,
            Long level1CategoryId,
            Long level2CategoryId,
            int limit // size + 1
    );
}
