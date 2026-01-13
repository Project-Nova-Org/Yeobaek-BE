package com.nova.yeobaek.domain.calendar.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CalendarRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConnectOotdRequest {

        @NotNull(message = "ootdId는 필수입니다.")
        private Long ootdId;
    }
}
