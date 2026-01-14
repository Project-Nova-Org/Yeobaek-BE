package com.nova.yeobaek.domain.calendar.dto.response;

import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail;

public class CalendarResponseDTO {

    public record EntryDetailResponse(
            String date,                 // YYYY-MM-DD
            Thumbnail thumbnail,          // CUSTOM / OOTD (없으면 null)
            String thumbnailImageUrl,     // 썸네일 이미지 URL (없으면 null)
            OotdInfo ootd,                // 없으면 null
            String customImageUrl         // 없으면 null
    ) {
        public static EntryDetailResponse empty(String date) {
            return new EntryDetailResponse(
                    date,
                    null,
                    null,
                    null,
                    null
            );
        }
    }

    public record OotdInfo(
            Long ootdId,
            String ootdImageUrl
    ) {}

    public record EntryDeleteResponse(
            String date,
            boolean deleted
    ) {}
}