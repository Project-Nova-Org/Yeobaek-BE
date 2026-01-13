package com.nova.yeobaek.domain.calendar.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nova.yeobaek.domain.calendar.converter.CalendarConverter;
import com.nova.yeobaek.domain.calendar.domain.Calendar;
import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail;
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO;
import com.nova.yeobaek.domain.calendar.repository.CalendarRepository;
import com.nova.yeobaek.domain.calendar.status.CalendarErrorStatus;
import com.nova.yeobaek.domain.ootd.domain.OOTD;
import com.nova.yeobaek.domain.ootd.repository.OOTDRepository;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.repository.UserRepository;
import com.nova.yeobaek.global.payload.exception.GeneralException;
import com.nova.yeobaek.global.payload.status.CommonErrorStatus;

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
        LocalDate parsedDate = parseDateOrThrow(date);

        return calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .map(calendarConverter::toEntryDetailResponse)
                .orElse(CalendarResponseDTO.EntryDetailResponse.empty(date));
    }

    /**
     * ✅ POST /api/calendar/entries/{date}
     * - "기록 없는 날짜"는 row 자체가 없어야 하므로, 여기서만 row를 생성한다.
     * - ootdId 필수
     * - ootd.createdAt.toLocalDate() == date 검증
     * - 이미 존재하면 CONFLICT4006 (DUPLICATED_CALENDAR_CREATE)
     */
    public CalendarResponseDTO.EntryDetailResponse createEntry(Long userId, String date, Long ootdId) {
        LocalDate parsedDate = parseDateOrThrow(date);

        OOTD ootd = ootdRepository.findById(ootdId)
                .orElseThrow(() -> new GeneralException(CalendarErrorStatus.CALENDAR4041));

        if (ootd.getCreatedAt() == null || !ootd.getCreatedAt().toLocalDate().equals(parsedDate)) {
            throw new GeneralException(CalendarErrorStatus.OOTD_NOT_IN_DATE);
        }

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
            // UNIQUE(user_id, date) 충돌
            throw new GeneralException(CommonErrorStatus.DUPLICATED_CALENDAR_CREATE);
        }

        return calendarConverter.toEntryDetailResponse(calendar);
    }

    /**
     * ✅ DELETE /api/calendar/entries/{date}
     * - OOTD 연결 해제 ❌
     * - calendars row 자체 삭제 ✅
     * - 없으면 CALENDAR4040
     */
    public CalendarResponseDTO.EntryDeleteResponse deleteEntry(Long userId, String date) {
        LocalDate parsedDate = parseDateOrThrow(date);

        Calendar calendar = calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .orElseThrow(() -> new GeneralException(CalendarErrorStatus.CALENDAR4040));

        calendarRepository.delete(calendar);

        return CalendarResponseDTO.EntryDeleteResponse.builder()
                .date(parsedDate.toString())
                .deleted(true)
                .build();
    }

    /**
     * ✅ POST /api/calendar/entries/{date}/custom-image
     * - calendars row 반드시 존재해야 함 (없으면 CALENDAR4040)
     */
    public CalendarResponseDTO.EntryDetailResponse addCustomImage(Long userId, String date, String imageUrl) {
        LocalDate parsedDate = parseDateOrThrow(date);

        Calendar calendar = calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .orElseThrow(() -> new GeneralException(CalendarErrorStatus.CALENDAR4040));

        calendar.setCustomImage(imageUrl);

        return calendarConverter.toEntryDetailResponse(calendar);
    }

    /**
     * ✅ DELETE /api/calendar/entries/{date}/custom-image
     * - custom 이미지만 삭제
     * - 대표가 CUSTOM이었다면 thumbnail = OOTD로 복귀
     */
    public CalendarResponseDTO.EntryDetailResponse deleteCustomImage(Long userId, String date) {
        LocalDate parsedDate = parseDateOrThrow(date);

        Calendar calendar = calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .orElseThrow(() -> new GeneralException(CalendarErrorStatus.CALENDAR4040));

        if (calendar.getCustomImageUrl() == null || calendar.getCustomImageUrl().isBlank()) {
            throw new GeneralException(CalendarErrorStatus.CALENDAR4043);
        }

        calendar.removeCustomImageAndFallbackThumbnail();

        return calendarConverter.toEntryDetailResponse(calendar);
    }

    /**
     * ✅ PATCH /api/calendar/entries/{date}/thumbnail
     * - OOTD | CUSTOM
     * - CUSTOM 선택 시 custom_image_url 필수
     */
    public CalendarResponseDTO.EntryDetailResponse updateThumbnail(Long userId, String date, Thumbnail thumbnail) {
        LocalDate parsedDate = parseDateOrThrow(date);

        Calendar calendar = calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .orElseThrow(() -> new GeneralException(CalendarErrorStatus.CALENDAR4040));

        if (thumbnail == Thumbnail.CUSTOM) {
            if (calendar.getCustomImageUrl() == null || calendar.getCustomImageUrl().isBlank()) {
                throw new GeneralException(CalendarErrorStatus.CUSTOM_IMAGE_REQUIRED);
            }
        }

        calendar.changeThumbnail(thumbnail);

        return calendarConverter.toEntryDetailResponse(calendar);
    }

    private LocalDate parseDateOrThrow(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new GeneralException(CalendarErrorStatus.INVALID_DATE);
        }
    }
}
