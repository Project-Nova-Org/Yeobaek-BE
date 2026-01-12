package com.nova.yeobaek.domain.calendar.controller.docs;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import com.nova.yeobaek.domain.calendar.dto.response.CalendarResponseDTO;
import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.payload.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Calendar", description = "달력 API")
public interface CalendarControllerDocs {

    /**
     * 선택한 날짜의 OOTD 기록 상세를 조회한다.
     *
     * <p>다음 동작을 따른다:
     * <ul>
     *   <li>CUSTOM 이미지가 존재하면 썸네일 타입은 CUSTOM.</li>
     *   <li>CUSTOM이 없고 OOTD가 존재하면 썸네일 타입은 OOTD.</li>
     *   <li>기록이 없으면 HTTP 200과 빈 결과 구조를 반환.</li>
     *   <li>date 형식이 잘못된 경우 COMMON400을 반환.</li>
     * </ul>
     *
     * @param date 조회 날짜(YYYY-MM-DD), 예: "2025-06-28"
     * @return CommonResponse에 래핑된 CalendarResponseDTO.EntryDetailResponse; 해당 날짜의 OOTD 상세(또는 기록 없음 시 빈 결과)를 포함한다.
     */
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
}