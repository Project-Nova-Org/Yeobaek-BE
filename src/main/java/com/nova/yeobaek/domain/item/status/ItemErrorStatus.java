package com.nova.yeobaek.domain.item.status;

import org.springframework.http.HttpStatus;

import com.nova.yeobaek.global.payload.status.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemErrorStatus implements ErrorReason {

	// 400 Bad Request
	COLOR_REQUIRED(
			HttpStatus.BAD_REQUEST,
			"ITEM4001",
			"색상은 최소 1개 이상 선택해야 합니다."
	),

	COLOR_COUNT_EXCEEDED(
			HttpStatus.BAD_REQUEST,
			"ITEM4002",
			"색상은 최대 2개까지 선택할 수 있습니다."
	),

	INVALID_COLOR(
			HttpStatus.BAD_REQUEST,
			"ITEM4003",
			"유효하지 않은 색상입니다."
	),

	SEASON_REQUIRED(
			HttpStatus.BAD_REQUEST,
			"ITEM4004",
			"계절은 최소 1개 이상 선택해야 합니다."
	),

	INVALID_SEASON(
			HttpStatus.BAD_REQUEST,
			"ITEM4005",
			"유효하지 않은 계절입니다."
	),

	INVALID_MATERIAL(
			HttpStatus.BAD_REQUEST,
			"ITEM4006",
			"유효하지 않은 소재입니다."
	),

	INVALID_IMAGE_BACKGROUND(
			HttpStatus.BAD_REQUEST,
			"ITEM4007",
			"유효하지 않은 이미지 배경 색상입니다."
	),

	// 404 Not Found
	CATEGORY_NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"ITEM4041",
			"존재하지 않는 카테고리입니다."
	),

	ITEM_NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"ITEM4042",
			"존재하지 않거나 접근할 수 없는 아이템입니다."
	);

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
