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
                    선택한 날짜의 OOTD 기록 상세를 조회합니다.
                    - CUSTOM 이미지가 존재하면 썸네일 타입은 CUSTOM
                    - CUSTOM이 없고 OOTD가 존재하면 썸네일 타입은 OOTD
                    - 기록이 없으면 200 + 빈 result 구조 반환
                    - date 형식이 틀리면 COMMON400
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDetailResponse> getEntryDetail(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "조회 날짜 (YYYY-MM-DD)", example = "2025-06-28")
            @PathVariable("date") String date
    );

    @Operation(
            summary = "날짜 캘린더 이미지 생성(OOTD 연결)",
            description = """
                    특정 날짜에 OOTD를 연결하거나 기존 기록을 수정합니다.
                    - 하루 1개의 캘린더 기록만 유지 (user + date 기준)
                    - 대표 이미지를 OOTD로 설정합니다.
                    - date 형식이 틀리면 COMMON400 (date 형식이 올바르지 않습니다.)
                    - OOTD가 없으면 CALENDAR4041
                    - 해당 날짜 엔트리가 없으면 CALENDAR4042
                    - 해당 날짜에 속하지 않은 OOTD면 COMMON400
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDetailResponse> connectOotd(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "대상 날짜 (YYYY-MM-DD)", example = "2025-06-28")
            @PathVariable("date") String date,

            // ✅ 구현체(CalendarController)와 동일하게 제약(@Valid) 맞춤
            @RequestBody @Valid CalendarRequestDTO.ConnectOotdRequest request
    );

    @Operation(
            summary = "날짜 캘린더 이미지 삭제(OOTD 연결 내역 삭제)",
            description = """
                    선택한 날짜의 OOTD 연결 내역을 삭제합니다.
                    - 캘린더 엔트리 row를 삭제하는 것이 아니라 ootd 연결만 해제합니다.
                    - date 형식이 틀리면 COMMON400 (date 형식이 올바르지 않습니다.)
                    - 해당 날짜 캘린더 기록이 없으면 CALENDAR4040
                    """
    )
    CommonResponse<CalendarResponseDTO.EntryDeleteResponse> disconnectOotd(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "user") User user,

            @Parameter(description = "대상 날짜 (YYYY-MM-DD)", example = "2025-06-28")
            @PathVariable("date") String date
    );
}
