package com.nova.yeobaek.domain.calendar.converter;

import org.springframework.stereotype.Component;

import com.nova.yeobaek.domain.calendar.domain.Calendar;
import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail;
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO;

@Component
public class CalendarConverter {

    public CalendarResponseDTO.EntryDetailResponse toEntryDetailResponse(Calendar calendar) {
        boolean hasCustom = calendar.getCustomImageUrl() != null && !calendar.getCustomImageUrl().isBlank();
        boolean hasOotd = calendar.getOotdImageUrl() != null && !calendar.getOotdImageUrl().isBlank(); // row 존재 시 사실상 true

        Thumbnail thumbnail = null;
        String thumbnailImageUrl = null;

        // ✅ 대표 이미지 규칙
        if (hasOotd && hasCustom) {
            // 둘 다 있으면 DB 컬럼 thumbnail 기준
            thumbnail = calendar.getThumbnail();
            thumbnailImageUrl = (thumbnail == Thumbnail.CUSTOM) ? calendar.getCustomImageUrl() : calendar.getOotdImageUrl();
        } else if (hasCustom) {
            thumbnail = Thumbnail.CUSTOM;
            thumbnailImageUrl = calendar.getCustomImageUrl();
        } else if (hasOotd) {
            thumbnail = Thumbnail.OOTD;
            thumbnailImageUrl = calendar.getOotdImageUrl();
        }

        CalendarResponseDTO.EntryDetailResponse.OotdInfo ootdInfo = null;
        if (calendar.getOotd() != null) {
            ootdInfo = CalendarResponseDTO.EntryDetailResponse.OotdInfo.builder()
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
