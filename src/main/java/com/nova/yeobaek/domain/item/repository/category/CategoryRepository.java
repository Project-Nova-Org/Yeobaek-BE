package com.nova.yeobaek.domain.item.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.item.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
