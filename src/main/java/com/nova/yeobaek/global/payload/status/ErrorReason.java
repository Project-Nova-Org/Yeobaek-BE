package com.nova.yeobaek.global.payload.status;

import org.springframework.http.HttpStatus;

public interface ErrorReason {

	HttpStatus getHttpStatus();
	String getCode();
	String getMessage();
}
