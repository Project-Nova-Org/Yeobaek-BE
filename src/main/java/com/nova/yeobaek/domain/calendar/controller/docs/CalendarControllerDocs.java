package com.nova.yeobaek.domain.calendar.controller.docs;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail;
import com.nova.yeobaek.domain.calendar.dto.request.CalendarRequestDTO;
import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Calendar", description = "달력 API")
public interface CalendarControllerDocs {

    // =========================
    // 날짜 단위 API
    // =========================

    @Operation(
            summary = "날짜 상세 조회",
            description = """
                    선택한 날짜의 OOTD 기록 상세를 조회합니다.
                    - 기록이 있으면 해당 날짜의 썸네일/이미지 정보를 반환합니다.
                    - 기록이 없으면 200 + 빈 result 구조를 반환합니다.
                    - date 형식이 틀리면 COMMON400
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDetailResponse> getEntryDetail(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "조회 날짜 (YYYY-MM-DD)", example = "2026-01-23")
            @PathVariable("date") String date
    );

    @Operation(
            summary = "날짜 OOTD 기록 생성",
            description = """
                    선택한 날짜에 OOTD 기록을 생성합니다.
                    - 기록 없는 날짜는 calendars row가 없으므로, 이 API에서만 row를 생성합니다.
                    - ootdId는 필수입니다.
                    - 이미 해당 날짜에 기록이 존재하면 409(CALENDAR4090)
                    - date 형식이 틀리면 COMMON400
                    - ootdId가 존재하지 않으면 404(CALENDAR4041)
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDetailResponse> createEntry(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "생성 날짜 (YYYY-MM-DD)", example = "2026-01-23")
            @PathVariable("date") String date,

            @RequestBody @Valid CalendarRequestDTO.CreateEntryRequest request
    );

    @Operation(
            summary = "날짜 OOTD 기록 삭제",
            description = """
                    선택한 날짜의 캘린더 기록을 삭제합니다.
                    - calendars row 자체가 삭제됩니다.
                    - 기록이 없으면 404(CALENDAR4040)
                    - date 형식이 틀리면 COMMON400
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDeleteResponse> deleteEntry(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "삭제 날짜 (YYYY-MM-DD)", example = "2026-01-23")
            @PathVariable("date") String date
    );

    @Operation(
            summary = "날짜 커스텀 이미지 추가",
            description = """
                    선택한 날짜의 캘린더 기록에 커스텀 이미지를 저장합니다.
                    - calendars row가 반드시 존재해야 합니다. (없으면 404(CALENDAR4040))
                    - date 형식이 틀리면 COMMON400
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDetailResponse> addCustomImage(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "대상 날짜 (YYYY-MM-DD)", example = "2026-01-23")
            @PathVariable("date") String date,

            @RequestBody @Valid CalendarRequestDTO.CustomImageRequest request
    );

    @Operation(
            summary = "날짜 커스텀 이미지 삭제",
            description = """
                    선택한 날짜의 커스텀 이미지만 삭제합니다.
                    - calendars row가 반드시 존재해야 합니다. (없으면 404(CALENDAR4040))
                    - 삭제할 customImageUrl이 없으면 404(CALENDAR4043)
                    - date 형식이 틀리면 COMMON400
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDetailResponse> deleteCustomImage(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "대상 날짜 (YYYY-MM-DD)", example = "2026-01-23")
            @PathVariable("date") String date
    );

    @Operation(
            summary = "날짜 썸네일 타입 변경",
            description = """
                    날짜 썸네일 타입을 변경합니다. (OOTD | CUSTOM)
                    - CUSTOM 선택 시 customImageUrl이 반드시 존재해야 합니다. (없으면 COMMON400)
                    - calendars row가 없으면 404(CALENDAR4040)
                    - date 형식이 틀리면 COMMON400
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDetailResponse> updateThumbnail(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "대상 날짜 (YYYY-MM-DD)", example = "2026-01-23")
            @PathVariable("date") String date,

            @RequestBody @Valid CalendarRequestDTO.UpdateThumbnailRequest request
    );

    // =========================
    // 월 캘린더 API
    // =========================

    @Operation(
            summary = "월 캘린더 조회",
            description = """
                    선택한 월의 캘린더 기록을 조회합니다. (6x7 = 42칸)
                    - 주 시작 요일: 월요일 기준으로 grid를 구성합니다.
                    - 최근 3개월(현재월 포함)만 조회 가능합니다.
                      예) 현재가 2026-01이면 2025-11, 2025-12, 2026-01만 허용
                    - 3개월 초과 과거 월/미래 월 요청 시 COMMON400
                    - yearMonth 형식이 틀리면 COMMON400
                    """
    )
    CommonResponse<CalendarResponseDTO.MonthlyCalendarResponse> getMonthlyCalendar(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "조회 월 (YYYY-MM)", example = "2026-01")
            @PathVariable("yearMonth") String yearMonth
    );

    @Operation(
            summary = "월 이미지 조회",
            description = """
                    해당 월의 월 대표 이미지를 조회합니다.
                    - ERD: user_histories.monthly_ootd_image_url 조회
                    - 과거 월 포함 조회 가능합니다.
                    - 해당 월 이미지가 없으면 404(CALENDAR4044)
                    - yearMonth 형식이 틀리면 COMMON400
                    """
    )
    CommonResponse<CalendarResponseDTO.MonthImageResponse> getMonthImage(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "조회 월 (YYYY-MM)", example = "2026-01")
            @PathVariable("yearMonth") String yearMonth
    );

    @Operation(
            summary = "월 이미지 저장",
            description = """
                    월 대표 이미지를 저장(생성/갱신)합니다.
                    - request body: { "monthlyOotdImageUrl": "https://..." }
                    - ERD: user_histories.monthly_ootd_image_url 저장
                    - 최근 3개월(현재월 포함)만 저장 가능합니다. (그 외 COMMON400)
                    - yearMonth 형식이 틀리면 COMMON400
                    """
    )
    CommonResponse<CalendarResponseDTO.MonthImageSaveResponse> saveMonthImage(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "저장 월 (YYYY-MM)", example = "2026-01")
            @PathVariable("yearMonth") String yearMonth,

            @RequestBody @Valid CalendarRequestDTO.SaveMonthImageRequest request
    );
}
