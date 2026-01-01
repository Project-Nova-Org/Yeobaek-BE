package com.nova.yeobaek.global.payload.status;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessStatus {

	_OK(HttpStatus.OK, "COMMON200", "요청이 성공했습니다."),
	_CREATED(HttpStatus.CREATED, "COMMON201", "리소스를 생성했습니다."),
	_ACCEPTED(HttpStatus.ACCEPTED, "COMMON202", "요청이 접수되었습니다."),
	;

	private final HttpStatus status;
	private final String code;
	private final String message;
}
