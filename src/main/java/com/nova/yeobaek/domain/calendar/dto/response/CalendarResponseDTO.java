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
            CalendarMode mode,            // NORMAL / IMAGE_ONLY
            List<DaySummary> days,        // IMAGE_ONLY면 empty list
            String monthImageUrl          // IMAGE_ONLY에서 보여줄 월 대표 이미지(없으면 null)
    ) {}

    /**
     * NORMAL: 최근 3개월 이내 → days 채움
     * IMAGE_ONLY: 3개월 초과 → days 비움 + monthImageUrl 사용
     */
    public enum CalendarMode {
        NORMAL,
        IMAGE_ONLY
    }

    public record DaySummary(
            String date,                  // YYYY-MM-DD

            boolean hasOotd,              // ootd 존재 여부(프론트 분기용)
            boolean hasCustomImage,       // custom 이미지 존재 여부(프론트 분기용)

            Thumbnail thumbnail,          // CUSTOM / OOTD (기록 없는 날짜는 null)
            String thumbnailImageUrl,     // 최종 썸네일 URL (없으면 null)

            String ootdImageUrl,          // nullable
            String customImageUrl         // nullable
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
