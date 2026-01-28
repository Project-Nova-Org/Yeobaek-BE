package com.nova.yeobaek.domain.item.repository.material;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.item.domain.Material;

import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByName(String name);
}
