package com.nova.yeobaek.domain.closet.repository.closetItem;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.closet.domain.mapping.ClosetItem;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ClosetItemRepository extends JpaRepository<ClosetItem, Long>, ClosetItemRepositoryCustom {
    @Query("select ci from ClosetItem ci join fetch ci.item where ci.closet.id = :closetId")
    List<ClosetItem> findAllWithItemByClosetId(@Param("closetId") Long closetId);
}
