package com.nova.yeobaek.domain.calendar.controller.docs;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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

    @Operation(
            summary = "날짜 상세 조회",
            description = """
                    선택한 날짜의 기록 상세를 조회합니다.
                    - 기록이 없으면 200 + empty response
                    - date 형식이 틀리면 COMMON400
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDetailResponse> getEntryDetail(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "조회 날짜 (YYYY-MM-DD)", example = "2026-01-13")
            @PathVariable("date") String date
    );

    @Operation(
            summary = "날짜 기록 생성",
            description = """
                    빈 날짜(기록 없음)에 캘린더 기록을 생성합니다.
                    - POST에서만 calendars row 생성
                    - OOTD 필수 (ootdId 필요)
                    - 이미 해당 날짜 row가 있으면 CONFLICT4006
                    - ootd가 없으면 CALENDAR4041
                    - ootd가 해당 날짜가 아니면 COMMON400
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDetailResponse> createEntry(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "대상 날짜 (YYYY-MM-DD)", example = "2026-01-13")
            @PathVariable("date") String date,

            @RequestBody @Valid CalendarRequestDTO.CreateEntryRequest request
    );

    @Operation(
            summary = "날짜 기록 삭제",
            description = """
                    해당 날짜 calendars row 자체를 삭제합니다.
                    - OOTD 연결 해제 
                    - row delete 
                    - 기록이 없으면 CALENDAR4040
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDeleteResponse> deleteEntry(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "대상 날짜 (YYYY-MM-DD)", example = "2026-01-13")
            @PathVariable("date") String date
    );

    @Operation(
            summary = "커스텀 이미지 추가",
            description = """
                    해당 날짜 calendars row에 커스텀 이미지를 저장합니다.
                    - calendars row가 반드시 존재해야 함 (없으면 CALENDAR4040)
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDetailResponse> addCustomImage(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "대상 날짜 (YYYY-MM-DD)", example = "2026-01-13")
            @PathVariable("date") String date,

            @RequestBody @Valid CalendarRequestDTO.CustomImageRequest request
    );

    @Operation(
            summary = "커스텀 이미지 삭제",
            description = """
                    해당 날짜 calendars row에서 커스텀 이미지만 삭제합니다.
                    - 대표가 CUSTOM이었다면 thumbnail=OOTD로 자동 복귀
                    - 커스텀이 없으면 CALENDAR4043
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDetailResponse> deleteCustomImage(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "대상 날짜 (YYYY-MM-DD)", example = "2026-01-13")
            @PathVariable("date") String date
    );

    @Operation(
            summary = "대표 이미지 선택",
            description = """
                    대표 이미지를 OOTD | CUSTOM 으로 선택합니다.
                    - CUSTOM 선택 시 custom_image_url 필수 (없으면 COMMON400)
                    - calendars row가 없으면 CALENDAR4040
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDetailResponse> updateThumbnail(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "대상 날짜 (YYYY-MM-DD)", example = "2026-01-13")
            @PathVariable("date") String date,

            @RequestBody @Valid CalendarRequestDTO.UpdateThumbnailRequest request
    );

    // =========================
    //  월 캘린더 API
    // =========================

    @Operation(
            summary = "월 캘린더 조회",
            description = """
                    해당 월의 달력 grid(6x7, 42일)를 조회합니다.
                    - 주 시작 요일: 월요일
                    - 최근 3개월 이내면 42일 grid 반환
                    - 3개월 초과 또는 미래 월이면 days = []
                    - 기록 없는 날짜(캘린더 row 없음)는 DaySummary의 thumbnail/ootdImageUrl/customImageUrl이 모두 null로 반환됩니다.
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
                    월 대표 이미지를 조회합니다.
                    - ERD: user_histories.monthly_ootd_image_url 사용
                    - 월 이미지가 없으면 CALENDAR4044
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
                    월 대표 이미지를 저장합니다.
                    - request body: { "monthlyOotdImageUrl": "https://..." }
                    - ERD: user_histories.monthly_ootd_image_url 저장
                    - 최근 3개월 이내만 가능 (아니면 COMMON400)
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
