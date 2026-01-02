package com.nova.yeobaek.global.auth.oauth.exception;

import com.nova.yeobaek.global.auth.oauth.exception.code.OAuthErrorStatus;
import com.nova.yeobaek.global.payload.exception.GeneralException;

public class OAuthException extends GeneralException {
    public OAuthException(OAuthErrorStatus status) {
        super(status);
    }
}
