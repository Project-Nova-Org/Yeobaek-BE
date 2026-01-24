package com.nova.yeobaek.domain.ootd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.ootd.domain.enums.OOTDStatus;

public interface OOTDRepository extends JpaRepository<OOTD, Long>, OOTDRepositoryCustom {

    @Modifying
    @Query("UPDATE OOTD o SET o.status = :status WHERE o.id IN " +
            "(SELECT oi.ootd.id FROM OOTDItem oi WHERE oi.item.id = :itemId)")
    void updateStatusByItemId(@Param("itemId") Long itemId, @Param("status") OOTDStatus status);
}
