package com.nova.yeobaek.domain.ootd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.ootd.domain.enums.OOTDStatus;

import java.util.List;

public interface OOTDRepository extends JpaRepository<OOTD, Long>, OOTDRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE OOTD o SET o.status = :status, o.updatedAt = CURRENT_TIMESTAMP WHERE o.id IN " +
            "(SELECT oi.ootd.id FROM OOTDItem oi WHERE oi.item.id = :itemId)")
    void updateStatusByItemId(@Param("itemId") Long itemId, @Param("status") OOTDStatus status);

    @Query("SELECT DISTINCT o FROM OOTD o " +
            "JOIN o.ootdItemList oi " +
            "WHERE oi.item.id = :itemId " +
            "AND o.user.id = :userId " +
            "AND o.status = 'NORMAL' " +
            "ORDER BY o.id DESC")
    List<OOTD> findAllByItemIdAndUserId(@Param("itemId") Long itemId, @Param("userId") Long userId);
}
