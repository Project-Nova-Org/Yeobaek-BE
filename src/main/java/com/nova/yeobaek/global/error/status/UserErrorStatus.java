package com.nova.yeobaek.global.error.status;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorStatus implements ErrorReason {

	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
