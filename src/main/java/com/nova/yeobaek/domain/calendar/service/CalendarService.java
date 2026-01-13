package com.nova.yeobaek.domain.calendar.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nova.yeobaek.domain.calendar.converter.CalendarConverter;
import com.nova.yeobaek.domain.calendar.domain.Calendar;
import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail; // ✅ 프로젝트에 맞게 존재해야 함 (OOTD/CUSTOM)
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO;
import com.nova.yeobaek.domain.calendar.repository.CalendarRepository;
import com.nova.yeobaek.domain.calendar.status.CalendarErrorStatus;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.ootd.repository.OOTDRepository;
import com.nova.yeobaek.domain.user.repository.UserRepository;
import com.nova.yeobaek.global.payload.exception.GeneralException;

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

    @Transactional(readOnly = true)
    public CalendarResponseDTO.EntryDetailResponse getEntryDetail(Long userId, String date) {
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date); // YYYY-MM-DD
        } catch (DateTimeParseException e) {
            throw new GeneralException(CalendarErrorStatus.INVALID_DATE);
        }

        return calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .map(calendarConverter::toEntryDetailResponse)
                .orElse(CalendarResponseDTO.EntryDetailResponse.empty(date));
    }

    /**
     * ✅ Upsert 버전
     *
     * POST /api/calendar/entries/{date}
     * - 엔트리 있으면 update
     * - 엔트리 없으면 insert 후 update (자동 생성)
     * - ootd 존재해야 함 (없으면 CALENDAR4041)
     * - ootd.createdAt.toLocalDate() == date 여야 함 (아니면 OOTD_NOT_IN_DATE)
     */
    public CalendarResponseDTO.EntryDetailResponse connectOotd(Long userId, String date, Long ootdId) {
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new GeneralException(CalendarErrorStatus.INVALID_DATE);
        }

        // ✅ OOTD 존재 검증
        OOTD ootd = ootdRepository.findById(ootdId)
                .orElseThrow(() -> new GeneralException(CalendarErrorStatus.CALENDAR4041));

        // ✅ "해당 날짜에 속하지 않은 OOTD" 검증
        if (ootd.getCreatedAt() == null || !ootd.getCreatedAt().toLocalDate().equals(parsedDate)) {
            throw new GeneralException(CalendarErrorStatus.OOTD_NOT_IN_DATE);
        }

        // ✅ 엔트리 없으면 자동 생성(upsert)
        Calendar calendar = getOrCreateCalendar(userId, parsedDate);

        // ✅ 대표 이미지를 OOTD로 설정
        calendar.connectOotd(ootd);

        return calendarConverter.toEntryDetailResponse(calendar);
    }

    /**
     * DELETE /api/calendar/entries/{date}
     * - row 삭제가 아니라 "OOTD 연결 내역 삭제" (ootd=null)
     * - ✅ Upsert 정책에서 DELETE는 보통 멱등(idempotent) 처리:
     *   엔트리가 없어도 200으로 "삭제됨" 응답 (프론트/테스트 편함)
     */
    public CalendarResponseDTO.EntryDeleteResponse disconnectOotd(Long userId, String date) {
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new GeneralException(CalendarErrorStatus.INVALID_DATE);
        }

        Calendar calendar = calendarRepository.findByUserIdAndDate(userId, parsedDate).orElse(null);

        if (calendar != null) {
            calendar.disconnectOotd();
        }

        return CalendarResponseDTO.EntryDeleteResponse.builder()
                .date(parsedDate.toString())
                .deleted(true)
                .build();
    }

    /**
     * calendars 테이블 제약(NOT NULL / uk_user_date) 때문에
     * 엔트리 없으면 "기본값" 채워서 생성해야 함.
     */
    private Calendar getOrCreateCalendar(Long userId, LocalDate date) {
        return calendarRepository.findByUserIdAndDate(userId, date)
                .orElseGet(() -> {
                    try {
                        Calendar created = Calendar.builder()
                                // FK만 잡고 싶으면 getReferenceById가 제일 가벼움
                                .user(userRepository.getReferenceById(userId))
                                .date(date)

                                // ✅ DB에서 ootd_image_url NOT NULL 이라 빈 문자열로라도 채워야 함
                                .ootdImageUrl("")

                                // ✅ thumbnail NOT NULL (DB default가 있더라도 안전하게 세팅)
                                .thumbnail(Thumbnail.OOTD)

                                // ootdId는 연결 전이므로 null
                                .ootd(null)

                                // customImageUrl은 null 가능
                                .customImageUrl(null)
                                .build();

                        return calendarRepository.save(created);

                    } catch (DataIntegrityViolationException e) {
                        // 동시성/중복 요청으로 uk_user_date에 걸릴 수 있음 -> 다시 조회해서 반환
                        return calendarRepository.findByUserIdAndDate(userId, date)
                                .orElseThrow(() -> e);
                    }
                });
    }
}
