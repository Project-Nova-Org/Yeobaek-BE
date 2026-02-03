package com.nova.yeobaek.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.user.domain.UserHistory;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
    Optional<UserHistory> findByUser_IdAndYearMonth(Long userId, String yearMonth);
}
