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
import com.nova.yeobaek.domain.user.repository.UserHistoryRepository;
import com.nova.yeobaek.domain.user.repository.UserRepository;
import com.nova.yeobaek.domain.user.service.ItemUsageService;

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

    // ✅ EntityManager 제거 → Repository로 책임 분리
    private final UserHistoryRepository userHistoryRepository;

    // ✅ 아이템 사용횟수 증감 서비스
    private final ItemUsageService itemUsageService;

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
     * POST /api/calendar/entries/{date}
     * - 기록 없는 날짜만 row 생성
     * - ootdId 필수
     * - 이미 존재하면 DUPLICATED_CALENDAR_CREATE
     */
    public CalendarResponseDTO.EntryDetailResponse createEntry(Long userId, String date, Long ootdId) {
        LocalDate parsedDate = parseDateOrThrow(date);

        OOTD ootd = ootdRepository.findById(ootdId)
                .orElseThrow(() -> new CalendarException(CalendarErrorStatus.CALENDAR4041));

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

        // ✅ (마지막 기능) 캘린더에 ootd 등록 → 해당 ootd의 item 사용횟수 증가
        itemUsageService.increaseByOotd(userRef, ootd.getId(), parsedDate);

        return calendarConverter.toEntryDetailResponse(calendar);
    }

    /**
     * DELETE /api/calendar/entries/{date}
     * - calendars row 자체 삭제
     * - 없으면 CALENDAR4040
     */
    public CalendarResponseDTO.EntryDeleteResponse deleteEntry(Long userId, String date) {
        LocalDate parsedDate = parseDateOrThrow(date);

        Calendar calendar = calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .orElseThrow(() -> new CalendarException(CalendarErrorStatus.CALENDAR4040));

        User userRef = userRepository.getReferenceById(userId);

        // ✅ (마지막 기능) 캘린더에서 ootd 삭제 → 해당 ootd의 item 사용횟수 감소
        if (calendar.getOotd() != null) {
            itemUsageService.decreaseByOotd(userRef, calendar.getOotd().getId());
        }

        calendarRepository.delete(calendar);

        return new CalendarResponseDTO.EntryDeleteResponse(
                parsedDate.toString(),
                true
        );
    }

    /**
     * POST /api/calendar/entries/{date}/custom-image
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
     * DELETE /api/calendar/entries/{date}/custom-image
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
     * PATCH /api/calendar/entries/{date}/thumbnail
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
    // 월 캘린더 로직
    // =========================

    /**
     * GET /api/calendars/months/{yearMonth}
     * - 최근 3개월 이내: NORMAL 모드로 6x7(42칸) grid 반환
     * - 3개월 초과/미래 월: IMAGE_ONLY 모드로 days=[] 반환
     * - 주 시작 요일: 월요일
     */
    @Transactional(readOnly = true)
    public CalendarResponseDTO.MonthlyCalendarResponse getMonthlyCalendar(Long userId, String yearMonth) {
        YearMonth ym = parseYearMonthOrThrow(yearMonth);

        if (!isRecent3Months(ym)) {
            String monthImageUrl = findMonthlyImageUrlOrNull(userId, ym.toString());

            return new CalendarResponseDTO.MonthlyCalendarResponse(
                    yearMonth,
                    CalendarResponseDTO.CalendarMode.IMAGE_ONLY,
                    List.of(),
                    monthImageUrl
            );
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
                days.add(new CalendarResponseDTO.DaySummary(
                        d.toString(),
                        false,
                        false,
                        null,
                        null,
                        null,
                        null
                ));
            } else {
                boolean hasCustom = c.getCustomImageUrl() != null && !c.getCustomImageUrl().isBlank();
                boolean hasOotd = c.getOotdImageUrl() != null && !c.getOotdImageUrl().isBlank();

                // ✅ thumbnail 필드 값을 기준으로 실제 썸네일 URL을 결정 (일관성 유지)
                String thumbnailImageUrl = resolveThumbnailImageUrl(c, hasOotd, hasCustom);

                days.add(new CalendarResponseDTO.DaySummary(
                        d.toString(),
                        hasOotd,
                        hasCustom,
                        c.getThumbnail(),
                        thumbnailImageUrl,
                        c.getOotdImageUrl(),
                        c.getCustomImageUrl()
                ));
            }
        }

        return new CalendarResponseDTO.MonthlyCalendarResponse(
                yearMonth,
                CalendarResponseDTO.CalendarMode.NORMAL,
                days,
                null
        );
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
     * - 미래 월 저장은 막음
     */
    public CalendarResponseDTO.MonthImageSaveResponse saveMonthImage(Long userId, String yearMonth, String imageUrl) {
        YearMonth ym = parseYearMonthOrThrow(yearMonth);

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

            userHistoryRepository.save(history);
        } else {
            history.changeMonthlyOotdImageUrl(imageUrl);
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
        if (target.isAfter(now)) return false;

        return target.equals(now)
                || target.equals(now.minusMonths(1))
                || target.equals(now.minusMonths(2));
    }

    private UserHistory findUserHistoryOrNull(Long userId, String yearMonth) {
        return userHistoryRepository.findByUser_IdAndYearMonth(userId, yearMonth).orElse(null);
    }

    private String findMonthlyImageUrlOrNull(Long userId, String yearMonth) {
        UserHistory history = findUserHistoryOrNull(userId, yearMonth);
        if (history == null) return null;

        String url = history.getMonthlyOotdImageUrl();
        if (url == null || url.isBlank()) return null;

        return url;
    }

    /**
     * ✅ 썸네일 타입(thumbnail)을 기준으로 thumbnailImageUrl 결정
     * - 사용자가 OOTD를 선택했으면 OOTD URL
     * - 사용자가 CUSTOM을 선택했으면 CUSTOM URL
     * - 혹시 데이터가 비정상일 때(선택값에 해당하는 URL이 없음) 안전 fallback 적용
     */
    private String resolveThumbnailImageUrl(Calendar c, boolean hasOotd, boolean hasCustom) {
        Thumbnail t = c.getThumbnail();

        if (t == Thumbnail.CUSTOM) {
            if (hasCustom) return c.getCustomImageUrl();
            if (hasOotd) return c.getOotdImageUrl(); // fallback
            return null;
        }

        if (t == Thumbnail.OOTD) {
            if (hasOotd) return c.getOotdImageUrl();
            if (hasCustom) return c.getCustomImageUrl(); // fallback
            return null;
        }

        // t가 null/예외값인 경우 안전 처리
        if (hasCustom) return c.getCustomImageUrl();
        if (hasOotd) return c.getOotdImageUrl();
        return null;
    }
}
