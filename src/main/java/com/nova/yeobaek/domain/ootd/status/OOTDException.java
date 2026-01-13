package com.nova.yeobaek.domain.ootd.status;

import com.nova.yeobaek.global.payload.exception.GeneralException;

public class OOTDException extends GeneralException {

    public OOTDException(OOTDErrorStatus errorStatus) {
        super(errorStatus);
    }
}