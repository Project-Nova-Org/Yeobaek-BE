package com.nova.yeobaek.global.error.status;

import org.springframework.http.HttpStatus;

public interface ErrorReason {

	HttpStatus getHttpStatus();
	String getCode();
	String getMessage();
}
