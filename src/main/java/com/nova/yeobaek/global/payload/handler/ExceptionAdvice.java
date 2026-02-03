package com.nova.yeobaek.global.payload.handler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.nova.yeobaek.global.payload.exception.GeneralException;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import com.nova.yeobaek.global.payload.status.CommonErrorStatus;
import com.nova.yeobaek.global.payload.status.ErrorReason;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

	private static final Map<String, CommonErrorStatus> CONSTRAINT_ERROR_MAP = Map.of(
		"uk_brand_name", CommonErrorStatus.DUPLICATED_BRAND_NAME,
		"uk_user_year_month", CommonErrorStatus.DUPLICATED_HISTORY_MONTH,
		"uk_closet_item", CommonErrorStatus.DUPLICATED_CLOSET_ITEM,
		"uk_user_item", CommonErrorStatus.DUPLICATED_ITEM_USAGE,
		"uk_device_token", CommonErrorStatus.DUPLICATED_DEVICE_TOKEN,
		"uk_user_date", CommonErrorStatus.DUPLICATED_CALENDAR_CREATE,
		"uk_oauth_provider_id", CommonErrorStatus.DUPLICATED_OAUTH_PROVIDER_ID,
		"uk_closet_user_name", CommonErrorStatus.DUPLICATED_CLOSET_USER_NAME
	);

	// 응답 통일 - String
	private ResponseEntity<Object> handleExceptionInternal(ErrorReason errorReason, String message) {
		// 예외에서 별도 메시지를 주지 않으면(ErrorReason 기반 예외) 기본 정의된 message 사용
		String finalMessage = (message != null && !message.isBlank()) ? message : errorReason.getMessage();
		CommonResponse<Object> body = CommonResponse.onFailure(errorReason.getCode(), finalMessage, null);
		return ResponseEntity.status(errorReason.getHttpStatus()).body(body);
	}

	// 응답 통일 - Map<String, String>
	private ResponseEntity<Object> handleExceptionInternalArgs(ErrorReason errorReason, Map<?, ?> map) {
		CommonResponse<Object> body = CommonResponse.onFailure(errorReason.getCode(), errorReason.getMessage(), map);
		return ResponseEntity.status(errorReason.getHttpStatus()).body(body);
	}

	// DataIntegrityViolationException 핸들링
	// Unique Constraints 등 제약 조건 위반 시 발생하는 예외 처리
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
		String errorMessage = e.getMessage();
		log.error("DataIntegrityViolationException occurred: {}", errorMessage);

		String constraintName;
		if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException hibernateException) {
			constraintName = hibernateException.getConstraintName();
		} else {
			constraintName = null;
		}

		CommonErrorStatus status = CONSTRAINT_ERROR_MAP.entrySet().stream()
			.filter(entry -> constraintName != null && constraintName.contains(entry.getKey()))
			.map(Map.Entry::getValue)
			.findFirst()
			.orElse(CommonErrorStatus._INTERNAL_SERVER_ERROR);

		return handleExceptionInternal(status, status.getMessage());
	}

	// ConstrainViolationException 핸들링
	// Custom Validation 사용 시 발생하는 예외 처리
	@ExceptionHandler(value = ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e) {
		String errorMessage = e.getConstraintViolations().stream()
			.map(ConstraintViolation::getMessage)
			.findFirst()
			.orElseThrow(() -> new GeneralException(CommonErrorStatus.CONSTRAINTS_VIOLATION_EXCEPTION_ERROR));
		return handleExceptionInternal(CommonErrorStatus._BAD_REQUEST, errorMessage);
	}

	// MethodArgumentNotValid 핸들링
	// @Valid로 DTO 필드 유효성 검사 시 발생하는 예외
	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers,
		HttpStatusCode status, WebRequest request) {
		Map<String, String> errors = new LinkedHashMap<>();
		e.getBindingResult().getFieldErrors().forEach(fieldError -> {
			String fieldName = fieldError.getField();
			String errorMessage = Optional.of(fieldError.getDefaultMessage()).orElse("잘못된 입력값입니다.");
			errors.merge(fieldName, errorMessage,
				(existing, newMessage) -> existing + ", " + newMessage);
		});

		return handleExceptionInternalArgs(CommonErrorStatus.METHOD_ARGUMENT_NOT_VALID, errors);
	}

	// HttpMessageNotReadableException 핸들링
	// JSON을 입력받아 DTO로 변환하기 전, 형식이나 타입이 유효하지 않을 때 발생하는 예외
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpHeaders headers,
		HttpStatusCode status, WebRequest request) {
		Throwable cause = e.getCause();
		String details;

		if (cause instanceof InvalidFormatException ife) {
			List<JsonMappingException.Reference> path = ife.getPath();
			if (!path.isEmpty()) {
				String fieldName = path.getFirst().getFieldName();
				String targetType = ife.getTargetType().getSimpleName();
				details = String.format("'%s' 필드는 '%s' 타입이어야 합니다.", fieldName, targetType);
			} else {
				details = "형식 변환 중 에러가 발생했습니다.";
			}
		} else if (cause instanceof MismatchedInputException mie) {
			List<JsonMappingException.Reference> path = mie.getPath();
			if (!path.isEmpty()) {
				String fieldName = path.getFirst().getFieldName();
				details = String.format("'%s' 필드의 값이 누락되었거나 형식이 올바르지 않습니다.", fieldName);
			} else {
				details = "요청 데이터의 형식이 맞지 않습니다.";
			}
		} else if (cause instanceof JsonParseException) {
			details = "JSON 문법 오류가 발생했습니다.";
		} else {
			details = "요청 본문을 읽을 수 없습니다.";
		}
		return handleExceptionInternal(CommonErrorStatus.TYPE_OR_FORMAT_NOT_VALID, details);
	}

	// GeneralException 핸들링
	// throw new GeneralException(ErrorReason.*)로 처리하는 예외
	@ExceptionHandler(value = GeneralException.class)
	public ResponseEntity<Object> handleGeneralException(GeneralException e, HttpServletRequest request) {
		log.warn("Business Exception: {} | URI: {}", e.getMessage(), request.getRequestURI());
		return handleExceptionInternal(e.getErrorReason(), e.getMessage());
	}

	// 기타 에러 핸들링
	// 따로 설정하지 않은 모든 예외
	@ExceptionHandler
	public ResponseEntity<Object> handleException(Exception e, HttpServletRequest request) {
		log.error("Unhandled Exception: [{} {}] ", request.getMethod(), request.getRequestURI(), e);
		return handleExceptionInternal(CommonErrorStatus._INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
	}
}
