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
