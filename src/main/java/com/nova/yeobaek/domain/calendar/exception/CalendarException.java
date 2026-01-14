package com.nova.yeobaek.domain.calendar.exception;

import com.nova.yeobaek.domain.calendar.status.CalendarErrorStatus;
import com.nova.yeobaek.global.payload.exception.GeneralException;

public class CalendarException extends GeneralException {

    public CalendarException(CalendarErrorStatus status) {
        super(status);
    }
}
