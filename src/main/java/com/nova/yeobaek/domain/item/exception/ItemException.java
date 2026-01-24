package com.nova.yeobaek.domain.item.exception;

import com.nova.yeobaek.domain.item.status.ItemErrorStatus;
import com.nova.yeobaek.global.payload.exception.GeneralException;

public class ItemException extends GeneralException {

    public ItemException(ItemErrorStatus status) {
        super(status);
    }
}
