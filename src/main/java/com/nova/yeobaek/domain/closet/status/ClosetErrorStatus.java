package com.nova.yeobaek.domain.closet.status;

import org.springframework.http.HttpStatus;

import com.nova.yeobaek.global.payload.status.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClosetErrorStatus implements ErrorReason {

	INVALID_NAME(
			HttpStatus.BAD_REQUEST,
			"CLOSET_4001",
			"특수문자는 사용할 수 없습니다."
	),

	EMPTY_ITEMS(
			HttpStatus.BAD_REQUEST,
			"CLOSET_4002",
			"하나 이상의 아이템이 포함되어야 합니다."
	),

	DUPLICATED_ITEM_ID(
			HttpStatus.BAD_REQUEST,
			"CLOSET_4003",
			"중복된 아이템 ID가 포함되어 있습니다."
	),
	DUPLICATED_NAME(
			HttpStatus.BAD_REQUEST,
			"CLOSET_4003",
			"이미 존재하는 옷장 이름입니다."
	),


	INVALID_COVER_IMAGE_URL(
			HttpStatus.BAD_REQUEST,
			"CLOSET_4004",
			"유효하지 않은 썸네일 이미지입니다."
	),
	ITEM_NOT_FOUND(
			HttpStatus.BAD_REQUEST,
			"CLOSET_4005",
			"존재하지 않는 아이템이 포함되어 있습니다."
	),

	SERVER_ERROR(
			HttpStatus.INTERNAL_SERVER_ERROR,
			"CLOSET_5001",
			"서버 오류 발생."
	);



	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
