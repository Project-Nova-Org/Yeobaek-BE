package com.nova.yeobaek.domain.closet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.closet.domain.Closet;

public interface ClosetRepository extends JpaRepository<Closet, Long>, ClosetRepositoryCustom {
}
