package com.nova.yeobaek.global.auth.exception;

import com.nova.yeobaek.global.payload.exception.GeneralException;
import com.nova.yeobaek.global.payload.status.ErrorReason;

public class AuthException extends GeneralException {
    public AuthException(ErrorReason message) {
        super(message);
    }
}
