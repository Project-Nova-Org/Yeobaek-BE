package com.nova.yeobaek.global.payload.code;

import org.springframework.http.HttpStatus;

import com.nova.yeobaek.global.payload.ErrorReason;

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
