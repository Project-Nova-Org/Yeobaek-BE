package com.nova.yeobaek.domain.user.repository.itemUsage;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.user.domain.mapping.ItemUsage;

public interface ItemUsageRepository extends JpaRepository<ItemUsage, Long>, ItemUsageRepositoryCustom {

    Optional<ItemUsage> findByUser_IdAndItem_Id(Long userId, Long itemId);

    List<ItemUsage> findAllByUser_IdOrderByUseCountDesc(Long userId);
}
