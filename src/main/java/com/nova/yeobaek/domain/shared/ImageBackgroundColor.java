package com.nova.yeobaek.domain.shared;

import java.util.Arrays;

public enum ImageBackgroundColor {
	WHITE, GRAY;

	/**
	 - 문자열을 ImageBackgroundColor로 변환
	 - 잘못된 값이면 IllegalArgumentException 발생
	 */
	public static ImageBackgroundColor from(String value) {

		return Arrays.stream(values())
				.filter(color -> color.name().equalsIgnoreCase(value))
				.findFirst()
				.orElseThrow(() ->
						new IllegalArgumentException("Invalid ImageBackgroundColor: " + value)
				);
	}
}