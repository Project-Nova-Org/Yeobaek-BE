package com.nova.yeobaek.domain.closet.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClosetEditRequestDTO {

    public record Update(
            @NotBlank(message = "name은 필수입니다.")
            @Pattern(
                    regexp = "^[a-zA-Z0-9가-힣\\s]+$",
                    message = "name은 영문/숫자/한글/공백만 허용됩니다."
            )
            @Size(max = 30, message = "name은 최대 30자까지 허용됩니다.")
            String name,

            @Size(max = 500, message = "imageUrl은 최대 500자까지 허용됩니다.")
            String imageUrl,

            @NotNull(message = "itemIds는 필수입니다.")
            List<Long> itemIds
    ) {
    }
}
