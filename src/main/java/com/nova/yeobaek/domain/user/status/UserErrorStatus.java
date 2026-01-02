package com.nova.yeobaek.domain.user.status;

import org.springframework.http.HttpStatus;

import com.nova.yeobaek.global.payload.status.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorStatus implements ErrorReason {

	USER_NOT_FOUND(HttpStatus.NOT_FOUND,"AUTH_4041","사용자를 찾을 수 없습니다."),
	DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST,"AUTH_4002","중복된 닉네임을 선택했습니다."),
	ALREADY_SIGNUP(HttpStatus.BAD_REQUEST,"AUTH_4001","이미 닉네임을 설정한 유저입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
