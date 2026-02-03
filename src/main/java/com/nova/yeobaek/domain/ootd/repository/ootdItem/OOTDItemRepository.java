package com.nova.yeobaek.domain.ootd.repository.ootdItem;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.nova.yeobaek.domain.ootd.domain.mapping.OOTDItem;

public interface OOTDItemRepository extends JpaRepository<OOTDItem, Long>, OOTDItemRepositoryCustom {
    void deleteAllByOotd_Id(Long ootdId);
    // 아이템 사용횟수 업데이트용 (추가)
    List<OOTDItem> findAllByOotd_Id(Long ootdId);
}
