package com.nova.yeobaek.global.payload.common;

import org.springframework.http.HttpStatus;

import com.nova.yeobaek.global.payload.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorStatus implements ErrorReason {

	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
	_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

	// JSON 관련
	JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "JSON4001", "JSON 파싱 중 에러가 발생했습니다."),

	// ExceptionAdvice 관련
	METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "ARGUMENT4001", "Argument Validation을 실패했습니다."),
	TYPE_OR_FORMAT_NOT_VALID(HttpStatus.BAD_REQUEST, "ARGUMENT4002", "Argument의 타입이나 형식이 올바르지 않습니다."),
	CONSTRAINTS_VIOLATION_EXCEPTION_ERROR(HttpStatus.BAD_REQUEST, "ARGUMENT4003", "ConstraintsViolationException 추출 도중 에러 발생"),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
