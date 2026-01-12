package com.nova.yeobaek.domain.ootd.status;

import org.springframework.http.HttpStatus;

import com.nova.yeobaek.global.payload.status.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OOTDErrorStatus implements ErrorReason {

	DUPLICATED_ITEM_ID(
			HttpStatus.BAD_REQUEST,
			"OOTD4001",
			"아이템 ID가 중복되었습니다."
	),

	ITEM_NOT_FOUND(
			HttpStatus.NOT_FOUND,
			"OOTD4041",
			"존재하지 않는 아이템이 포함되어 있습니다."
	);

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}