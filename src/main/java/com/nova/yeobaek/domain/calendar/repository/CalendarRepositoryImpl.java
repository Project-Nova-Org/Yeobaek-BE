package com.nova.yeobaek.domain.calendar.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.nova.yeobaek.domain.calendar.domain.Calendar;
import com.nova.yeobaek.domain.calendar.domain.QCalendar;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalendarRepositoryImpl implements CalendarRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Calendar> findByUserIdAndDate(Long userId, LocalDate date) {
        QCalendar calendar = QCalendar.calendar;

        Calendar result = queryFactory
                .selectFrom(calendar)
                .where(
                        // ✅ 기존: calendar.userId.eq(userId)  (필드 없음)
                        calendar.user.id.eq(userId),
                        calendar.date.eq(date)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
