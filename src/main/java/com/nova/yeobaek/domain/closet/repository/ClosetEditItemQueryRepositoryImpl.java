package com.nova.yeobaek.domain.closet.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class ClosetEditItemQueryRepositoryImpl implements ClosetEditItemQueryRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ClosetEditItemView> findEditableItemsForClosetEdit(
            Long userId,
            Long closetId,
            Long cursorId,
            Long level1CategoryId,
            Long level2CategoryId,
            int limit
    ) {

        String jpql = """
			select
				i.id as id,
				i.imageUrl as imageUrl,
				i.category.id as categoryId,
				case when exists (
					select 1
					from ClosetItem ci
					where ci.closet.id = :closetId
					  and ci.item.id = i.id
				) then true else false end as selectedStatus
			from Item i
			where i.user.id = :userId
			  and (:cursorId is null or i.id < :cursorId)
			  and (
					 (:level2CategoryId is not null and i.category.id = :level2CategoryId)
				  or (:level2CategoryId is null and :level1CategoryId is not null and i.category.parent.id = :level1CategoryId)
				  or (:level2CategoryId is null and :level1CategoryId is null)
			  )
			order by i.id desc
		""";

        return em.createQuery(jpql, ClosetEditItemView.class)
                .setParameter("userId", userId)
                .setParameter("closetId", closetId)
                .setParameter("cursorId", cursorId)
                .setParameter("level1CategoryId", level1CategoryId)
                .setParameter("level2CategoryId", level2CategoryId)
                .setMaxResults(limit)
                .getResultList();
    }
}
