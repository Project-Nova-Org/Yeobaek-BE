package com.nova.yeobaek.domain.closet.repository;

import java.util.List;
import java.util.Optional;

import com.nova.yeobaek.domain.closet.domain.mapping.ClosetItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nova.yeobaek.domain.closet.domain.Closet;
import com.nova.yeobaek.domain.user.domain.User;

public interface ClosetRepository extends JpaRepository<Closet, Long> {

    boolean existsByUserAndName(User user, String name);

    Optional<Closet> findByIdAndUser(Long id, User user);

    // 기존 LATEST/OLDEST 커서 메서드 유지(네가 이미 쓰는 것 그대로)
    List<Closet> findByUserOrderByIdDesc(User user, Pageable pageable);
    List<Closet> findByUserAndIdLessThanOrderByIdDesc(User user, Long cursorId, Pageable pageable);

    List<Closet> findByUserOrderByIdAsc(User user, Pageable pageable);
    List<Closet> findByUserAndIdGreaterThanOrderByIdAsc(User user, Long cursorId, Pageable pageable);

    @Query("""
        select c from Closet c
        where c.user = :user
        order by c.favorite desc, c.id desc
    """)
    List<Closet> findByUserOrderByFavoriteDescIdDesc(@Param("user") User user, Pageable pageable);

    @Query("""
        select c from Closet c
        where c.user = :user
          and (
            (case when c.favorite = true then 1 else 0 end) < :cursorFav
            or ((case when c.favorite = true then 1 else 0 end) = :cursorFav and c.id < :cursorId)
          )
        order by c.favorite desc, c.id desc
    """)
    List<Closet> findByUserAfterFavoriteDescIdDesc(
            @Param("user") User user,
            @Param("cursorFav") int cursorFav,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("""
        select c from Closet c
        where c.user = :user
        order by c.favorite desc, c.id asc
    """)
    List<Closet> findByUserOrderByFavoriteDescIdAsc(@Param("user") User user, Pageable pageable);

    @Query("""
        select c from Closet c
        where c.user = :user
          and (
            (case when c.favorite = true then 1 else 0 end) < :cursorFav
            or ((case when c.favorite = true then 1 else 0 end) = :cursorFav and c.id > :cursorId)
          )
        order by c.favorite desc, c.id asc
    """)
    List<Closet> findByUserAfterFavoriteDescIdAsc(
            @Param("user") User user,
            @Param("cursorFav") int cursorFav,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("""
    select ci
    from ClosetItem ci
    join fetch ci.item i
    where ci.closet.id = :closetId
      and (:cursorId is null or ci.id < :cursorId)
      and i.category.id = :level2CategoryId
    order by ci.id desc
""")
    List<ClosetItem> findItemsByClosetIdWithItemByCursorAndLevel2Category(
            Long closetId, Long cursorId, Long level2CategoryId, Pageable pageable
    );

    @Query("""
    select ci
    from ClosetItem ci
    join fetch ci.item i
    join i.category c
    where ci.closet.id = :closetId
      and (:cursorId is null or ci.id < :cursorId)
      and c.parent.id = :level1CategoryId
    order by ci.id desc
""")
    List<ClosetItem> findItemsByClosetIdWithItemByCursorAndLevel1Category(
            Long closetId, Long cursorId, Long level1CategoryId, Pageable pageable
    );

}
