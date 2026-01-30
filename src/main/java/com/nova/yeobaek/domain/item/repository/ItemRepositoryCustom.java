package com.nova.yeobaek.domain.item.repository;

import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.user.domain.User;

import java.util.List;

public interface ItemRepositoryCustom {

    List<Item> findItemList(
            User user,
            Long categoryId,
            String season,
            String material,
            String keyword,
            String sort,
            Long cursor,
            int limit
    );
}
