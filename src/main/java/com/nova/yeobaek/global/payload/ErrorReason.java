package com.nova.yeobaek.global.payload;

import org.springframework.http.HttpStatus;

public interface ErrorReason {

	HttpStatus getHttpStatus();
	String getCode();
	String getMessage();
}
