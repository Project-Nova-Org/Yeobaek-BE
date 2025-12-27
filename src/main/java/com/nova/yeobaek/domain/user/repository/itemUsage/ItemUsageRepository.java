package com.nova.yeobaek.domain.user.repository.itemUsage;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.user.domain.mapping.ItemUsage;

public interface ItemUsageRepository extends JpaRepository<ItemUsage, Long>, ItemUsageRepositoryCustom {
}
