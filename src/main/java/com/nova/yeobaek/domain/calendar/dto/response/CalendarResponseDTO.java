package com.nova.yeobaek.domain.calendar.dto.response;

import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CalendarResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntryDetailResponse {

        private String date;                 // YYYY-MM-DD
        private Thumbnail thumbnail;         // CUSTOM / OOTD (없으면 null)
        private String thumbnailImageUrl;    // 썸네일 이미지 URL (없으면 null)

        private OotdInfo ootd;               // 없으면 null
        private String customImageUrl;       // 없으면 null

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OotdInfo {
            private Long ootdId;
            private String ootdImageUrl;
        }

        /**
         * 지정한 날짜로 날짜만 채운 빈 EntryDetailResponse 인스턴스를 생성한다.
         *
         * @param date 날짜 문자열(형식: YYYY-MM-DD)
         * @return 지정된 날짜를 갖고 thumbnail, thumbnailImageUrl, ootd, customImageUrl 필드가 모두 `null`인 EntryDetailResponse 객체
         */
        public static EntryDetailResponse empty(String date) {
            return EntryDetailResponse.builder()
                    .date(date)
                    .thumbnail(null)
                    .thumbnailImageUrl(null)
                    .ootd(null)
                    .customImageUrl(null)
                    .build();
        }
    }
}