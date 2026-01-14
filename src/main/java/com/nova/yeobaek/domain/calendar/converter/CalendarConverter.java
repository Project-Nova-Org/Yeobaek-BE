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

        //  대표 이미지 규칙
        if (hasOotd && hasCustom) {
            // 둘 다 있으면 DB 컬럼 thumbnail 기준
            thumbnail = calendar.getThumbnail();
            thumbnailImageUrl = (thumbnail == Thumbnail.CUSTOM)
                    ? calendar.getCustomImageUrl()
                    : calendar.getOotdImageUrl();
        } else if (hasCustom) {
            thumbnail = Thumbnail.CUSTOM;
            thumbnailImageUrl = calendar.getCustomImageUrl();
        } else if (hasOotd) {
            thumbnail = Thumbnail.OOTD;
            thumbnailImageUrl = calendar.getOotdImageUrl();
        }

        //  record로 바뀌면 OotdInfo는 EntryDetailResponse 내부가 아니라 CalendarResponseDTO의 record로 사용
        CalendarResponseDTO.OotdInfo ootdInfo = null;
        if (calendar.getOotd() != null) {
            ootdInfo = new CalendarResponseDTO.OotdInfo(
                    calendar.getOotd().getId(),
                    calendar.getOotdImageUrl()
            );
        }

        //  builder 제거 (record 생성자 사용)
        return new CalendarResponseDTO.EntryDetailResponse(
                calendar.getDate().toString(),
                thumbnail,
                thumbnailImageUrl,
                ootdInfo,
                calendar.getCustomImageUrl()
        );
    }
}
