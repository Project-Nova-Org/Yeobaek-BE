package com.nova.yeobaek.domain.item.repository.material;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.item.domain.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {
}
