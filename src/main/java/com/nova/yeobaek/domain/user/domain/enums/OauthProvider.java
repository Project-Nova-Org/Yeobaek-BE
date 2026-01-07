package com.nova.yeobaek.domain.user.domain.enums;

import com.nova.yeobaek.global.auth.exception.AuthException;
import com.nova.yeobaek.global.auth.exception.code.AuthErrorStatus;

public enum OauthProvider {
	KAKAO, GOOGLE;

	public static OauthProvider from(String value) {
		if (value == null || value.isBlank()) {
			throw new AuthException(AuthErrorStatus.INVALID_PROVIDER);
		}

		try {
			return OauthProvider.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new AuthException(AuthErrorStatus.INVALID_PROVIDER);
		}
	}
}
