package com.nova.yeobaek.domain.calendar.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.nova.yeobaek.domain.calendar.domain.Calendar;

public interface CalendarRepositoryCustom {

    Optional<Calendar> findByUserIdAndDate(Long userId, LocalDate date);
}

