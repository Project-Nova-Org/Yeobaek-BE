package com.nova.yeobaek.domain.calendar.converter;

import org.springframework.stereotype.Component;

import com.nova.yeobaek.domain.calendar.domain.Calendar;
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO;
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO.EntryDetailResponse;
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO.EntryDetailResponse.OotdInfo;
import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail;

@Component
public class CalendarConverter {

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
