package com.nova.yeobaek.domain.item.repository.brand;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.item.domain.Brand;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);
}
