package com.nova.yeobaek.domain.ootd.repository.ootdItem;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.ootd.domain.mapping.OOTDItem;

public interface OOTDItemRepository extends JpaRepository<OOTDItem, Long>, OOTDItemRepositoryCustom {
    void deleteAllByOotd_Id(Long ootdId);
}
