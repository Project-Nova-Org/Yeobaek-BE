package com.nova.yeobaek.domain.calendar.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nova.yeobaek.domain.calendar.converter.CalendarConverter;
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO;
import com.nova.yeobaek.domain.calendar.repository.CalendarRepository;
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

    /**
     * 주어진 사용자와 날짜에 대한 캘린더 항목 상세를 조회한다.
     *
     * @param userId 조회할 사용자 ID
     * @param date   조회할 날짜 문자열 (YYYY-MM-DD 형식)
     * @return       존재하면 해당 항목의 상세 응답을 담은 `EntryDetailResponse`, 없으면 입력된 날짜를 가진 빈 `EntryDetailResponse`
     * @throws GeneralException 날짜 문자열이 파싱되지 않으면 `CommonErrorStatus._BAD_REQUEST`로 래핑된 예외를 던진다
     */
    @Transactional(readOnly = true)
    public CalendarResponseDTO.EntryDetailResponse getEntryDetail(Long userId, String date) {
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date); // YYYY-MM-DD
        } catch (DateTimeParseException e) {
            throw new GeneralException(CommonErrorStatus._BAD_REQUEST); // COMMON400
        }

        return calendarRepository.findByUserIdAndDate(userId, parsedDate)
                .map(calendarConverter::toEntryDetailResponse)
                .orElse(CalendarResponseDTO.EntryDetailResponse.empty(date));
    }
}