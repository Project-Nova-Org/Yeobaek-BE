package com.nova.yeobaek.domain.calendar.dto.response;

import java.util.List;

import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail;

public class CalendarResponseDTO {

    /* =========================
     * 날짜 상세 조회
     * ========================= */
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

    /* =========================
     * 월 캘린더 조회
     * ========================= */
    public record MonthlyCalendarResponse(
            String yearMonth,             // YYYY-MM
            List<DaySummary> days         // 최근 3개월 아니면 empty list
    ) {}

    public record DaySummary(
            String date,                  // YYYY-MM-DD
            Thumbnail thumbnail,           // CUSTOM / OOTD
            String ootdImageUrl,           // NOT NULL (기록 있는 날만 생성됨)
            String customImageUrl          // nullable
    ) {}

    /* =========================
     * 월 이미지
     * ========================= */
    public record MonthImageResponse(
            String yearMonth,
            String monthlyOotdImageUrl
    ) {}

    public record MonthImageSaveResponse(
            String yearMonth,
            String monthlyOotdImageUrl
    ) {}
}
