package com.nova.yeobaek.domain.calendar.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.nova.yeobaek.domain.calendar.domain.Calendar;

public interface CalendarRepositoryCustom {

    /**
 * 지정한 사용자와 날짜에 해당하는 Calendar 엔티티를 조회한다.
 *
 * @param userId 조회할 Calendar가 속한 사용자의 식별자
 * @param date 조회할 날짜 (년도-월-일)
 * @return 일치하는 Calendar가 존재하면 해당 엔티티를 담은 {@code Optional}, 없으면 빈 {@code Optional}
 */
Optional<Calendar> findByUserIdAndDate(Long userId, LocalDate date);
}
