package com.nova.yeobaek.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.nova.yeobaek.domain.item.domain.Item;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    List<Item> findAllByUser_Id(Long userId);
}
