package com.nova.yeobaek.domain.calendar.converter;

import org.springframework.stereotype.Component;

import com.nova.yeobaek.domain.calendar.domain.Calendar;
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO;
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO.EntryDetailResponse;
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO.EntryDetailResponse.OotdInfo;
import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail;

@Component
public class CalendarConverter {

    /**
     * Calendar 도메인 객체를 EntryDetailResponse DTO로 변환한다.
     *
     * 변환 시 커스텀 이미지가 있으면 썸네일으로 `CUSTOM`을 사용하고, 없으면 OOTD가 존재할 경우 `OOTD`를 사용한다.
     * OOTD가 존재하면 해당 OOTD의 아이디와 이미지 URL로 OotdInfo를 구성하여 응답에 포함한다.
     *
     * @param calendar 변환할 Calendar 도메인 객체
     * @return EntryDetailResponse DTO — 날짜, 선택된 썸네일 타입, 썸네일 이미지 URL, OOTD 정보(존재 시), 커스텀 이미지 URL을 포함한 응답 객체
     */
    public EntryDetailResponse toEntryDetailResponse(Calendar calendar) {
        Thumbnail thumbnail = null;
        String thumbnailImageUrl = null;

        // 1) CUSTOM 이미지가 있으면 CUSTOM 우선
        if (calendar.getCustomImageUrl() != null && !calendar.getCustomImageUrl().isBlank()) {
            thumbnail = Thumbnail.CUSTOM;
            thumbnailImageUrl = calendar.getCustomImageUrl();
        }
        // 2) CUSTOM 없고 OOTD 있으면 OOTD
        else if (calendar.getOotd() != null) {
            thumbnail = Thumbnail.OOTD;
            // Calendar 테이블에 ootdImageUrl이 필수로 있으니 그 값을 썸네일로 사용
            thumbnailImageUrl = calendar.getOotdImageUrl();
        }

        OotdInfo ootdInfo = null;
        if (calendar.getOotd() != null) {
            ootdInfo = OotdInfo.builder()
                    .ootdId(calendar.getOotd().getId())
                    .ootdImageUrl(calendar.getOotdImageUrl())
                    .build();
        }

        return CalendarResponseDTO.EntryDetailResponse.builder()
                .date(calendar.getDate().toString())
                .thumbnail(thumbnail)
                .thumbnailImageUrl(thumbnailImageUrl)
                .ootd(ootdInfo)
                .customImageUrl(calendar.getCustomImageUrl())
                .build();
    }
}