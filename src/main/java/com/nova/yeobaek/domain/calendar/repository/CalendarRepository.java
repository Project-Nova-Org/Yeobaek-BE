package com.nova.yeobaek.domain.calendar.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nova.yeobaek.domain.calendar.domain.Calendar;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    Optional<Calendar> findByUserIdAndDate(Long userId, LocalDate date);

    //  월 캘린더 조회(기간 내 기록 있는 날짜들만)
    List<Calendar> findAllByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    /** OOTD 삭제 연동용
     특정 사용자의 특정 OOTD가 기록된 모든 캘린더 엔트리 조회 */
    List<Calendar> findAllByUser_IdAndOotd_Id(Long userId, Long ootdId);

    // (선택) 월 로직엔 필수는 아니지만, 삭제/생성 로직에 쓰면 편함
    boolean existsByUserIdAndDate(Long userId, LocalDate date);
    void deleteByUserIdAndDate(Long userId, LocalDate date);

    // 만약 위 메서드들이 인식 안 되면 아래로 대체:
    // Optional<Calendar> findByUser_IdAndDate(Long userId, LocalDate date);
    // List<Calendar> findAllByUser_IdAndDateBetween(Long userId, LocalDate start, LocalDate end);
    // boolean existsByUser_IdAndDate(Long userId, LocalDate date);
    // void deleteByUser_IdAndDate(Long userId, LocalDate date);
}
