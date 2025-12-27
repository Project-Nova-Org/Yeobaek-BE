package com.nova.yeobaek.domain.ootd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.ootd.domain.OOTD;

public interface OOTDRepository extends JpaRepository<OOTD, Long>, OOTDRepositoryCustom {
}
