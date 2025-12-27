package com.nova.yeoback.global.error.exception;

import com.nova.yeoback.global.error.status.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

	private final ErrorReason errorReason;
}
