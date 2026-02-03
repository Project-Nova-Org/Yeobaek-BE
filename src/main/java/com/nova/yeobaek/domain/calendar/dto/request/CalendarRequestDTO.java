package com.nova.yeobaek.domain.calendar.dto.request;

import com.nova.yeobaek.domain.calendar.domain.enums.Thumbnail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CalendarRequestDTO {

    public record CreateEntryRequest(
            @NotNull(message = "ootdId는 필수입니다.")
            Long ootdId
    ) {}

    public record CustomImageRequest(
            @NotBlank(message = "imageUrl은 필수입니다.")
            String imageUrl
    ) {}

    public record UpdateThumbnailRequest(
            @NotNull(message = "thumbnail은 필수입니다.")
            Thumbnail thumbnail // OOTD | CUSTOM
    ) {}

    //  월 이미지 저장
    public record SaveMonthImageRequest(
            @NotBlank(message = "monthlyOotdImageUrl은 필수입니다.")
            String monthlyOotdImageUrl
    ) {}
}
