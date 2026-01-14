package com.nova.yeobaek.domain.ootd.status;

import org.springframework.http.HttpStatus;

import com.nova.yeobaek.global.payload.status.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OOTDErrorStatus implements ErrorReason {

	// 400
	DUPLICATED_ITEM_ID(
			HttpStatus.BAD_REQUEST,
			"OOTD4001",
			"아이템 ID가 중복되었습니다."
	),

	INVALID_IMAGE_BACKGROUND(
			HttpStatus.BAD_REQUEST,
			"OOTD4002",
			"유효하지 않은 이미지 배경 색상입니다."
	),

	// 404
	STYLE_NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"OOTD4041",
			"존재하지 않는 스타일입니다."
	),

	TPO_NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"OOTD4042",
			"존재하지 않는 TPO입니다."
	),

	ITEM_NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"OOTD4043",
			"존재하지 않는 아이템이 포함되어 있습니다."
	),

	OOTD_NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"OOTD4041",
			"존재하지 않거나 접근할 수 없는 OOTD입니다."
	);
	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}