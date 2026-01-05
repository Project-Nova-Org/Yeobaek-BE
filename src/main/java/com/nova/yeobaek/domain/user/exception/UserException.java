package com.nova.yeobaek.domain.user.exception;

import com.nova.yeobaek.global.payload.exception.GeneralException;
import com.nova.yeobaek.global.payload.status.ErrorReason;

public class UserException extends GeneralException {
    public UserException(ErrorReason status) {
        super(status);
    }
}
