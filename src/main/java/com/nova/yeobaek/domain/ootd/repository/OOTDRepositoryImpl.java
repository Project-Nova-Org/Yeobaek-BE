package com.nova.yeobaek.domain.ootd.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import lombok.RequiredArgsConstructor;

import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.user.domain.User;

@Repository
@RequiredArgsConstructor
public class OOTDRepositoryImpl implements OOTDRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<OOTD> findOOTDList(
            User user,
            String keyword,
            Boolean favorite,
            List<Long> tpoIds,
            List<Long> styleIds,
            String sort,
            Long cursor,
            int limit
    ) {
        StringBuilder jpql = new StringBuilder("""
                    SELECT o FROM OOTD o
                    WHERE o.user = :user
                      AND o.status = 'NORMAL'
                """);

        if (keyword != null && !keyword.isBlank()) {
            jpql.append(" AND o.name LIKE :keyword");
        }
        if (favorite != null) {
            jpql.append(" AND o.favorite = :favorite");
        }
        if (tpoIds != null && !tpoIds.isEmpty()) {
            jpql.append(" AND o.tpo.id IN :tpoIds");
        }
        if (styleIds != null && !styleIds.isEmpty()) {
            jpql.append(" AND o.style.id IN :styleIds");
        }
        if (cursor != null && !"NAME_ASC".equals(sort)) {
            jpql.append(" AND o.id < :cursor");
        }

        if ("NAME_ASC".equals(sort)) {
            jpql.append(" ORDER BY o.name ASC, o.id ASC");
        } else {
            jpql.append(" ORDER BY o.id DESC");
        }

        TypedQuery<OOTD> query = em.createQuery(jpql.toString(), OOTD.class)
                .setParameter("user", user)
                .setMaxResults(limit);

        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        if (favorite != null) {
            query.setParameter("favorite", favorite);
        }
        if (tpoIds != null && !tpoIds.isEmpty()) {
            query.setParameter("tpoIds", tpoIds);
        }
        if (styleIds != null && !styleIds.isEmpty()) {
            query.setParameter("styleIds", styleIds);
        }
        if (cursor != null && !"NAME_ASC".equals(sort)) {
            query.setParameter("cursor", cursor);
        }

        return query.getResultList();
    }
}