package com.nova.yeobaek.domain.calendar.status;

import org.springframework.http.HttpStatus;

import com.nova.yeobaek.global.payload.status.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalendarErrorStatus implements ErrorReason {

	// 400
	INVALID_DATE(HttpStatus.BAD_REQUEST, "COMMON400", "date 형식이 올바르지 않습니다."),
	OOTD_NOT_IN_DATE(HttpStatus.BAD_REQUEST, "COMMON400", "해당 날짜에 속하지 않은 OOTD입니다."),
	CUSTOM_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "COMMON400", "CUSTOM 선택 시 customImageUrl이 필요합니다."),

	// 404
	CALENDAR4040(HttpStatus.NOT_FOUND, "CALENDAR4040", "해당 날짜의 캘린더 기록이 존재하지 않습니다."),
	CALENDAR4041(HttpStatus.NOT_FOUND, "CALENDAR4041", "존재하지 않는 OOTD입니다."),
	CALENDAR4042(HttpStatus.NOT_FOUND, "CALENDAR4042", "해당 날짜의 캘린더 엔트리가 존재하지 않습니다."),
	CALENDAR4043(HttpStatus.NOT_FOUND, "CALENDAR4043", "삭제할 커스텀 이미지가 존재하지 않습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
