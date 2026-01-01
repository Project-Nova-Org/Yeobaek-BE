package com.nova.yeobaek.domain.closet.status;

import org.springframework.http.HttpStatus;

import com.nova.yeobaek.global.payload.status.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClosetErrorStatus implements ErrorReason {

	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
