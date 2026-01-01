package com.nova.yeobaek.global.payload.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.nova.yeobaek.global.payload.status.ErrorReason;
import com.nova.yeobaek.global.payload.status.SuccessStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"success", "code", "message", "result", "timestamp"})
public class CommonResponse<T> {

	@JsonProperty("success")
	private final boolean success;
	private final String code;
	private final String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final T result;
	private final LocalDateTime timestamp;

	// 200 OK
	public static <T> CommonResponse<T> onSuccess(T result) {
		return new CommonResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(),
			result, LocalDateTime.now());
	}

	// 201 CREATED
	public static <T> CommonResponse<T> onCreated(T result) {
		return new CommonResponse<>(true, SuccessStatus._CREATED.getCode(), SuccessStatus._CREATED.getMessage(),
			result, LocalDateTime.now());
	}

	// 202 ACCEPTED
	public static <T> CommonResponse<T> onAccepted(T result) {
		return new CommonResponse<>(true, SuccessStatus._ACCEPTED.getCode(), SuccessStatus._ACCEPTED.getMessage(),
			result, LocalDateTime.now());
	}

	// ExceptionAdvice
	public static <T> CommonResponse<T> onFailure(String code, String message, T result) {
		return new CommonResponse<>(false, code, message, result, LocalDateTime.now());
	}

	// Enum(ErrorReason) Failure
	public static <T> CommonResponse<T> onFailure(ErrorReason errorReason, T result) {
		return new CommonResponse<>(false, errorReason.getCode(), errorReason.getMessage(),
			result, LocalDateTime.now());
	}
}
