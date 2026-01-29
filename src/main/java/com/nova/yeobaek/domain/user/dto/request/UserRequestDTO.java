package com.nova.yeobaek.domain.user.dto.request;

import com.nova.yeobaek.domain.user.domain.enums.Gender;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

public class UserRequestDTO {
    public record PreferenceRequest(

            @Digits(integer = 3, fraction = 1)
            @DecimalMin("0.0")
            float height,
            @Digits(integer = 3, fraction = 1)
            @DecimalMin("0.0")
            float weight,
            Gender gender,
            String bodyImageUrl
    ) {
    }
}
