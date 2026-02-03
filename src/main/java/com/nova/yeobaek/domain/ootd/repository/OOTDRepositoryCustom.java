package com.nova.yeobaek.domain.ootd.repository;

import java.util.List;

import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.user.domain.User;

public interface OOTDRepositoryCustom {

    List<OOTD> findOOTDList(
            User user,
            String keyword,
            Boolean favorite,
            List<Long> tpoIds,
            List<Long> styleIds,
            String sort,
            Long cursor,
            String cursorName,
            int limit
    );
}