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

    /**
     * 주어진 사용자 ID와 날짜에 해당하는 Calendar 엔티티를 조회합니다.
     *
     * @param userId 조회할 Calendar의 소유자 사용자 ID
     * @param date 조회할 Calendar의 날짜
     * @return 주어진 사용자와 날짜에 맞는 Calendar를 포함한 Optional, 없으면 Optional.empty()
     */
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