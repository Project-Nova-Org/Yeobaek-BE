package com.nova.yeobaek.domain.calendar.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.calendar.domain.Calendar;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    Optional<Calendar> findByUserIdAndDate(Long userId, LocalDate date);
    // 만약 위 메서드가 인식 안 되면 아래로 대체:
    // Optional<Calendar> findByUser_IdAndDate(Long userId, LocalDate date);
}
