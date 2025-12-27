package com.nova.yeobaek.global.error.exception;

import com.nova.yeobaek.global.error.status.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

	private final ErrorReason errorReason;
}
