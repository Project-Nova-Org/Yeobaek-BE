package com.nova.yeobaek.domain.closet.repository.closetItem;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.closet.domain.mapping.ClosetItem;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;


public interface ClosetItemRepository extends JpaRepository<ClosetItem, Long>, ClosetItemRepositoryCustom {

    @Query("select ci from ClosetItem ci join fetch ci.item where ci.closet.id = :closetId")
    List<ClosetItem> findAllWithItemByClosetId(@Param("closetId") Long closetId);

    // added: cursor paging (id desc)
    @Query("""
        select ci from ClosetItem ci
        join fetch ci.item
        where ci.closet.id = :closetId
          and (:cursorId is null or ci.id < :cursorId)
        order by ci.id desc
    """)
    List<ClosetItem> findItemsByClosetIdWithItemByCursor(
            @Param("closetId") Long closetId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("""
        select ci
        from ClosetItem ci
        join fetch ci.item i
        join i.category c
        where ci.closet.id = :closetId
          and (:cursorId is null or ci.id < :cursorId)
          and c.parent.id = :level1CategoryId
        order by ci.id desc
    """)
    List<ClosetItem> findItemsByClosetIdWithItemByCursorAndLevel1Category(
            @Param("closetId") Long closetId,
            @Param("cursorId") Long cursorId,
            @Param("level1CategoryId") Long level1CategoryId,
            Pageable pageable
    );

    @Query("""
        select ci
        from ClosetItem ci
        join fetch ci.item i
        join i.category c
        where ci.closet.id = :closetId
          and (:cursorId is null or ci.id < :cursorId)
          and c.id = :level2CategoryId
        order by ci.id desc
    """)
    List<ClosetItem> findItemsByClosetIdWithItemByCursorAndLevel2Category(
            @Param("closetId") Long closetId,
            @Param("cursorId") Long cursorId,
            @Param("level2CategoryId") Long level2CategoryId,
            Pageable pageable
    );

}
