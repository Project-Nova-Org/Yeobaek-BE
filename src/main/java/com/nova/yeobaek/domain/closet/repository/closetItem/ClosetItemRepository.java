package com.nova.yeobaek.domain.closet.repository.closetItem;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.closet.domain.mapping.ClosetItem;

public interface ClosetItemRepository extends JpaRepository<ClosetItem, Long>, ClosetItemRepositoryCustom {
}
