package com.nova.yeobaek.domain.closet.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ClosetItemQuery(
        Long cursorId,
        @Min(1) @Max(100) Integer size,
        Long level1CategoryId,
        Long level2CategoryId
) {
    public Integer sizeOrDefault() {
        return (size == null) ? 20 : size;
    }
}
