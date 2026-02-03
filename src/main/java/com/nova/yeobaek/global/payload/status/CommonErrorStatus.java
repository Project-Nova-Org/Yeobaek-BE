package com.nova.yeobaek.global.payload.status;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorStatus implements ErrorReason {

	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
	_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
	_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청한 리소스를 찾을 수 없습니다."),

	// JSON 관련
	JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "JSON4001", "JSON 파싱 중 에러가 발생했습니다."),

	// ExceptionAdvice 관련
	METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "ARGUMENT4001", "Argument Validation을 실패했습니다."),
	TYPE_OR_FORMAT_NOT_VALID(HttpStatus.BAD_REQUEST, "ARGUMENT4002", "Argument의 타입이나 형식이 올바르지 않습니다."),
	CONSTRAINTS_VIOLATION_EXCEPTION_ERROR(HttpStatus.BAD_REQUEST, "ARGUMENT4003", "ConstraintsViolationException 추출 도중 에러 발생"),

	// cursor 관련
	INVALID_CURSOR(HttpStatus.BAD_REQUEST, "CURSOR4001", "이름 정렬 시 커서 ID와 이름이 모두 필요합니다."),

	// DataIntegrityViolationException 관련
	DUPLICATED_BRAND_NAME(HttpStatus.CONFLICT, "CONFLICT4001", "이미 존재하는 브랜드 이름입니다."),
	DUPLICATED_HISTORY_MONTH(HttpStatus.CONFLICT, "CONFLICT4002", "해당 월의 기록이 이미 존재합니다."),
	DUPLICATED_CLOSET_ITEM(HttpStatus.CONFLICT, "CONFLICT4003", "이미 옷장에 존재하는 아이템입니다."),
	DUPLICATED_ITEM_USAGE(HttpStatus.CONFLICT, "CONFLICT4004", "이미 사용 통계를 확인하고 있는 아이템입니다."),
	DUPLICATED_DEVICE_TOKEN(HttpStatus.CONFLICT, "CONFLICT4005", "해당 디바이스 토큰은 이미 존재합니다."),
	DUPLICATED_CALENDAR_CREATE(HttpStatus.CONFLICT, "CONFLICT4006", "해당 날짜에 캘린더가 이미 등록됐습니다."),
	DUPLICATED_OAUTH_PROVIDER_ID(HttpStatus.CONFLICT, "CONFLICT4007", "이미 존재하는 소셜 아이디와 타입 조합입니다."),
	DUPLICATED_CLOSET_USER_NAME(HttpStatus.CONFLICT, "CONFLICT4008", "이미 존재하는 옷장 이름입니다.")
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
