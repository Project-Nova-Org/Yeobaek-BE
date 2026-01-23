package com.nova.yeobaek.domain.calendar.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nova.yeobaek.domain.calendar.converter.CalendarConverter;
import com.nova.yeobaek.domain.calendar.domain.Calendar;
import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail;
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO;
import com.nova.yeobaek.domain.calendar.exception.CalendarException;
import com.nova.yeobaek.domain.calendar.repository.CalendarRepository;
import com.nova.yeobaek.domain.calendar.status.CalendarErrorStatus;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.ootd.repository.OOTDRepository;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.UserHistory;
import com.nova.yeobaek.domain.user.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final CalendarConverter calendarConverter;

    private final OOTDRepository ootdRepository;
    private final UserRepository userRepository;

    //  user_histories는 Repository 없이 EntityManager로 접근
    @PersistenceContext
    private EntityManager em;

    // =========================
    // 날짜 단위 API
    // =========================

    @Transactional(readOnly = true)
    public CalendarResponseDTO.EntryDetailResponse getEntryDetail(Long userId, String date) {
        LocalDate parsedDate = parseDateOrThrow(date);

        return calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .map(calendarConverter::toEntryDetailResponse)
                .orElse(CalendarResponseDTO.EntryDetailResponse.empty(date));
    }

    /**
     *  POST /api/calendar/entries/{date}
     * - "기록 없는 날짜"는 row 자체가 없어야 하므로, 여기서만 row를 생성한다.
     * - ootdId 필수
     * - ✅ (변경) ootd.createdAt.toLocalDate() == date 검증 제거: OOTD는 여러 날짜에 재사용 가능
     * - 이미 존재하면 DUPLICATED_CALENDAR_CREATE
     */
    public CalendarResponseDTO.EntryDetailResponse createEntry(Long userId, String date, Long ootdId) {
        LocalDate parsedDate = parseDateOrThrow(date);

        OOTD ootd = ootdRepository.findById(ootdId)
                .orElseThrow(() -> new CalendarException(CalendarErrorStatus.CALENDAR4041));

        // ✅ 제거됨: "해당 날짜에 속하지 않은 OOTD" 검증
        // 요구사항: OOTD는 독립 엔티티이며, Calendar가 날짜를 소유 → 동일 OOTD를 여러 날짜에 연결 가능해야 함
        // if (ootd.getCreatedAt() == null || !ootd.getCreatedAt().toLocalDate().equals(parsedDate)) {
        //     throw new CalendarException(CalendarErrorStatus.OOTD_NOT_IN_DATE);
        // }

        User userRef = userRepository.getReferenceById(userId);

        Calendar calendar = Calendar.builder()
                .user(userRef)
                .date(parsedDate)
                .ootd(ootd)
                .ootdImageUrl(ootd.getImageUrl())
                .customImageUrl(null)
                .thumbnail(Thumbnail.OOTD)
                .build();

        try {
            calendarRepository.save(calendar);
        } catch (DataIntegrityViolationException e) {
            throw new CalendarException(CalendarErrorStatus.DUPLICATED_CALENDAR_CREATE);
        }

        return calendarConverter.toEntryDetailResponse(calendar);
    }

    /**
     *  DELETE /api/calendar/entries/{date}
     * - calendars row 자체 삭제
     * - 없으면 CALENDAR4040
     */
    public CalendarResponseDTO.EntryDeleteResponse deleteEntry(Long userId, String date) {
        LocalDate parsedDate = parseDateOrThrow(date);

        Calendar calendar = calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .orElseThrow(() -> new CalendarException(CalendarErrorStatus.CALENDAR4040));

        calendarRepository.delete(calendar);

        return new CalendarResponseDTO.EntryDeleteResponse(
                parsedDate.toString(),
                true
        );
    }

    /**
     *  POST /api/calendar/entries/{date}/custom-image
     * - calendars row 반드시 존재해야 함 (없으면 CALENDAR4040)
     */
    public CalendarResponseDTO.EntryDetailResponse addCustomImage(Long userId, String date, String imageUrl) {
        LocalDate parsedDate = parseDateOrThrow(date);

        Calendar calendar = calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .orElseThrow(() -> new CalendarException(CalendarErrorStatus.CALENDAR4040));

        calendar.setCustomImage(imageUrl);

        return calendarConverter.toEntryDetailResponse(calendar);
    }

    /**
     *  DELETE /api/calendar/entries/{date}/custom-image
     * - custom 이미지만 삭제
     * - 삭제할 custom이 없으면 CALENDAR4043
     */
    public CalendarResponseDTO.EntryDetailResponse deleteCustomImage(Long userId, String date) {
        LocalDate parsedDate = parseDateOrThrow(date);

        Calendar calendar = calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .orElseThrow(() -> new CalendarException(CalendarErrorStatus.CALENDAR4040));

        if (calendar.getCustomImageUrl() == null || calendar.getCustomImageUrl().isBlank()) {
            throw new CalendarException(CalendarErrorStatus.CALENDAR4043);
        }

        calendar.removeCustomImageAndFallbackThumbnail();

        return calendarConverter.toEntryDetailResponse(calendar);
    }

    /**
     *  PATCH /api/calendar/entries/{date}/thumbnail
     * - OOTD | CUSTOM
     * - CUSTOM 선택 시 custom_image_url 필수
     */
    public CalendarResponseDTO.EntryDetailResponse updateThumbnail(Long userId, String date, Thumbnail thumbnail) {
        LocalDate parsedDate = parseDateOrThrow(date);

        Calendar calendar = calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .orElseThrow(() -> new CalendarException(CalendarErrorStatus.CALENDAR4040));

        if (thumbnail == Thumbnail.CUSTOM) {
            if (calendar.getCustomImageUrl() == null || calendar.getCustomImageUrl().isBlank()) {
                throw new CalendarException(CalendarErrorStatus.CUSTOM_IMAGE_REQUIRED);
            }
        }

        calendar.changeThumbnail(thumbnail);

        return calendarConverter.toEntryDetailResponse(calendar);
    }

    // =========================
    //  월 캘린더 로직
    // =========================

    /**
     * GET /api/calendars/months/{yearMonth}
     * - 최근 3개월 이내: 6x7(42칸) grid 반환
     * - 3개월 초과/미래 월: days=[]
     * - 주 시작 요일: 월요일
     */
    @Transactional(readOnly = true)
    public CalendarResponseDTO.MonthlyCalendarResponse getMonthlyCalendar(Long userId, String yearMonth) {
        YearMonth ym = parseYearMonthOrThrow(yearMonth);

        if (!isRecent3Months(ym)) {
            return new CalendarResponseDTO.MonthlyCalendarResponse(yearMonth, List.of());
        }

        LocalDate firstDay = ym.atDay(1);
        LocalDate gridStart = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate gridEnd = gridStart.plusDays(41);

        List<Calendar> records = calendarRepository.findAllByUserIdAndDateBetween(userId, gridStart, gridEnd);

        Map<LocalDate, Calendar> byDate = records.stream()
                .collect(Collectors.toMap(Calendar::getDate, c -> c, (a, b) -> a));

        List<CalendarResponseDTO.DaySummary> days = new ArrayList<>(42);

        for (int i = 0; i < 42; i++) {
            LocalDate d = gridStart.plusDays(i);
            Calendar c = byDate.get(d);

            if (c == null) {
                // 기록 없는 날짜: row 자체 없음 → grid에서 빈칸 처리
                days.add(new CalendarResponseDTO.DaySummary(
                        d.toString(),
                        null,
                        null,
                        null
                ));
            } else {
                days.add(new CalendarResponseDTO.DaySummary(
                        d.toString(),
                        c.getThumbnail(),
                        c.getOotdImageUrl(),
                        c.getCustomImageUrl()
                ));
            }
        }

        return new CalendarResponseDTO.MonthlyCalendarResponse(yearMonth, days);
    }

    /**
     * GET /api/calendars/months/{yearMonth}/image
     * - user_histories.monthlyOotdImageUrl 조회
     * - 없으면 CALENDAR4044
     */
    @Transactional(readOnly = true)
    public CalendarResponseDTO.MonthImageResponse getMonthImage(Long userId, String yearMonth) {
        YearMonth ym = parseYearMonthOrThrow(yearMonth);

        UserHistory history = findUserHistoryOrNull(userId, ym.toString());

        if (history == null || history.getMonthlyOotdImageUrl() == null || history.getMonthlyOotdImageUrl().isBlank()) {
            throw new CalendarException(CalendarErrorStatus.CALENDAR4044);
        }

        return new CalendarResponseDTO.MonthImageResponse(ym.toString(), history.getMonthlyOotdImageUrl());
    }

    /**
     * POST /api/calendars/months/{yearMonth}/image
     * - 월 대표 이미지를 저장/갱신한다. (과거 월 포함 허용)
     * - 최근 3개월은 "유저 선택 저장", 3개월 초과는 "자동 저장"이지만
     *   이는 프론트 정책이며 백엔드는 저장을 막지 않는다.
     */
    public CalendarResponseDTO.MonthImageSaveResponse saveMonthImage(Long userId, String yearMonth, String imageUrl) {
        YearMonth ym = parseYearMonthOrThrow(yearMonth);

        //  미래 월 저장은 막음
        YearMonth now = YearMonth.now();
        if (ym.isAfter(now)) {
            throw new CalendarException(CalendarErrorStatus.INVALID_YEAR_MONTH);
        }

        String ymStr = ym.toString();

        UserHistory history = findUserHistoryOrNull(userId, ymStr);

        if (history == null) {
            User userRef = userRepository.getReferenceById(userId);

            history = UserHistory.builder()
                    .user(userRef)
                    .yearMonth(ymStr)
                    .monthlyOotdImageUrl(imageUrl)
                    .build();

            em.persist(history);
        } else {
            history.changeMonthlyOotdImageUrl(imageUrl);
            // managed 상태면 merge 없어도 됨 (필요하면 유지 가능)
        }

        return new CalendarResponseDTO.MonthImageSaveResponse(ymStr, imageUrl);
    }

    // =========================
    // helpers
    // =========================

    private LocalDate parseDateOrThrow(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new CalendarException(CalendarErrorStatus.INVALID_DATE);
        }
    }

    private YearMonth parseYearMonthOrThrow(String yearMonth) {
        try {
            return YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (DateTimeParseException e) {
            throw new CalendarException(CalendarErrorStatus.INVALID_YEAR_MONTH);
        }
    }

    private boolean isRecent3Months(YearMonth target) {
        YearMonth now = YearMonth.now();

        // 미래 월 제외
        if (target.isAfter(now)) return false;

        return target.equals(now)
                || target.equals(now.minusMonths(1))
                || target.equals(now.minusMonths(2));
    }

    private UserHistory findUserHistoryOrNull(Long userId, String yearMonth) {
        try {
            return em.createQuery(
                            "select uh from UserHistory uh where uh.user.id = :userId and uh.yearMonth = :ym",
                            UserHistory.class
                    )
                    .setParameter("userId", userId)
                    .setParameter("ym", yearMonth)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
