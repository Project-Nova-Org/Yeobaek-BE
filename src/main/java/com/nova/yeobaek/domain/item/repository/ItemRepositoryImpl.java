package com.nova.yeobaek.domain.item.repository;

import com.nova.yeobaek.domain.item.domain.Item;
import com.nova.yeobaek.domain.item.domain.enums.Season;
import com.nova.yeobaek.domain.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Item> findItemList(
            User user,
            Long categoryId,
            String season,
            String material,
            String keyword,
            String sort,
            Long cursor,
            int limit
    ) {
        StringBuilder jpql = new StringBuilder("""
                SELECT DISTINCT i FROM Item i
                LEFT JOIN FETCH i.category
                LEFT JOIN FETCH i.material
                WHERE i.user = :user
                """);

        // 카테고리 필터
        if (categoryId != null) {
            jpql.append(" AND i.category.id = :categoryId");
        }

        // 계절 필터 (seasonSet에 포함되어 있는지 확인)
        if (season != null && !season.isBlank()) {
            jpql.append(" AND :season MEMBER OF i.seasonSet");
        }

        // 소재 필터
        if (material != null && !material.isBlank()) {
            jpql.append(" AND i.material.name = :material");
        }

        // 키워드 검색 (브랜드명, 메모)
        if (keyword != null && !keyword.isBlank()) {
            jpql.append(" AND (i.brandName LIKE :keyword OR i.memo LIKE :keyword)");
        }

        // 커서 페이징
        if (cursor != null && !"NAME_ASC".equals(sort)) {
            jpql.append(" AND i.id < :cursor");
        }

        // 정렬
        if ("NAME_ASC".equals(sort)) {
            jpql.append(" ORDER BY i.brandName ASC, i.id ASC");
        } else {
            jpql.append(" ORDER BY i.id DESC");
        }

        TypedQuery<Item> query = em.createQuery(jpql.toString(), Item.class)
                .setParameter("user", user)
                .setMaxResults(limit);

        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        if (season != null && !season.isBlank()) {
            try {
                query.setParameter("season", Season.valueOf(season.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // 유효하지 않은 계절인 경우 빈 결과 반환
                return List.of();
            }
        }
        if (material != null && !material.isBlank()) {
            query.setParameter("material", material);
        }
        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        if (cursor != null && !"NAME_ASC".equals(sort)) {
            query.setParameter("cursor", cursor);
        }

        return query.getResultList();
    }
}
