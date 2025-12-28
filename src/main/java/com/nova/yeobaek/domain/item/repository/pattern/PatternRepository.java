package com.nova.yeobaek.domain.item.repository.pattern;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.item.domain.Pattern;

public interface PatternRepository extends JpaRepository<Pattern, Long> {
}
