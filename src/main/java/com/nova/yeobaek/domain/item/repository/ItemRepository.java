package com.nova.yeobaek.domain.item.repository;

import com.nova.yeobaek.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nova.yeobaek.domain.item.domain.Item;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    Optional<Item> findByIdAndUser(Long id, User user);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM OOTDItem oi WHERE oi.item.id = :itemId")
    void bulkDeleteOOTDItemsByItemId(@Param("itemId") Long itemId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ClosetItem ci WHERE ci.item.id = :itemId")
    void bulkDeleteClosetItemsByItemId(@Param("itemId") Long itemId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ItemUsage iu WHERE iu.item.id = :itemId")
    void bulkDeleteItemUsagesByItemId(@Param("itemId") Long itemId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ItemsColor ic WHERE ic.item.id = :itemId")
    void bulkDeleteItemsColorsByItemId(@Param("itemId") Long itemId);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM seasons WHERE item_id = :itemId", nativeQuery = true)
    void bulkDeleteSeasonsByItemId(@Param("itemId") Long itemId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Item i WHERE i.id = :itemId")
    void bulkDeleteById(@Param("itemId") Long itemId);
}
