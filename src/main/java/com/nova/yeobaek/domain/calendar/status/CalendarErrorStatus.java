package com.nova.yeobaek.domain.calendar.status;

import org.springframework.http.HttpStatus;

import com.nova.yeobaek.global.payload.status.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalendarErrorStatus implements ErrorReason {

	// =======================
	// 400 BAD REQUEST
	// =======================

	INVALID_DATE(
			HttpStatus.BAD_REQUEST,
			"COMMON400",
			"date 형식이 올바르지 않습니다."
	),

	OOTD_NOT_IN_DATE(
			HttpStatus.BAD_REQUEST,
			"COMMON400",
			"해당 날짜에 속하지 않은 OOTD입니다."
	),

	CUSTOM_IMAGE_REQUIRED(
			HttpStatus.BAD_REQUEST,
			"COMMON400",
			"CUSTOM 선택 시 customImageUrl이 필요합니다."
	),

	// ----- 월 공통 -----
	INVALID_YEAR_MONTH(
			HttpStatus.BAD_REQUEST,
			"COMMON400",
			"yearMonth 형식이 올바르지 않습니다. (yyyy-MM)"
	),

	// ----- 월 캘린더 조회 -----
	MONTH_OUT_OF_RECENT_3MONTHS(
			HttpStatus.BAD_REQUEST,
			"COMMON400",
			"월 캘린더 조회는 최근 3개월 이내만 가능합니다."
	),

	// ----- 월 이미지 저장 -----
	MONTH_IMAGE_ONLY_RECENT_3MONTHS(
			HttpStatus.BAD_REQUEST,
			"COMMON400",
			"월 이미지 저장은 최근 3개월 이내만 가능합니다."
	),

	// =======================
	// 404 NOT FOUND
	// =======================

	CALENDAR4040(
			HttpStatus.NOT_FOUND,
			"CALENDAR4040",
			"해당 날짜의 캘린더 기록이 존재하지 않습니다."
	),

	CALENDAR4041(
			HttpStatus.NOT_FOUND,
			"CALENDAR4041",
			"존재하지 않는 OOTD입니다."
	),

	CALENDAR4043(
			HttpStatus.NOT_FOUND,
			"CALENDAR4043",
			"삭제할 커스텀 이미지가 존재하지 않습니다."
	),

	// ----- 월 이미지 조회 -----
	CALENDAR4044(
			HttpStatus.NOT_FOUND,
			"CALENDAR4044",
			"해당 월의 월 이미지가 존재하지 않습니다."
	),

	// =======================
	// 409 CONFLICT
	// =======================

	DUPLICATED_CALENDAR_CREATE(
			HttpStatus.CONFLICT,
			"CALENDAR4090",
			"이미 해당 날짜의 캘린더 기록이 존재합니다."
	);

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
