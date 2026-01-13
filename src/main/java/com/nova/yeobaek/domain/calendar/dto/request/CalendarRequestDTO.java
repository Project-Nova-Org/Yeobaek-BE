package com.nova.yeobaek.domain.calendar.dto.request;

import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail;

import jakarta.validation.constraints.NotBlank;
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
    public static class CreateEntryRequest {
        @NotNull(message = "ootdId는 필수입니다.")
        private Long ootdId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomImageRequest {
        @NotBlank(message = "imageUrl은 필수입니다.")
        private String imageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateThumbnailRequest {
        @NotNull(message = "thumbnail은 필수입니다.")
        private Thumbnail thumbnail; // OOTD | CUSTOM
    }
}
