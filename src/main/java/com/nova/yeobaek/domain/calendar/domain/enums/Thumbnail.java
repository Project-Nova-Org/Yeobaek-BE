package com.nova.yeobaek.domain.calendar.domain.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Thumbnail {
	OOTD,
	CUSTOM;

	@JsonCreator
	public static Thumbnail from(String value) {
		if (value == null) return null;

		return Arrays.stream(values())
				.filter(v -> v.name().equalsIgnoreCase(value)) // 대소문자까지 허용하려면
				.findFirst()
				.orElse(null); //  없는 값이면 null → DTO @NotNull에서 걸림
	}
}
