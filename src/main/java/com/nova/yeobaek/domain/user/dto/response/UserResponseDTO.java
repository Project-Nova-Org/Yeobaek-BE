package com.nova.yeobaek.domain.user.dto.response;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.enums.Gender;
import com.nova.yeobaek.domain.user.domain.enums.OauthProvider;
import com.nova.yeobaek.domain.user.domain.enums.Rank;

public class UserResponseDTO {
    public record PreferenceResponse(
            float height,
            float weight,
            Gender gender,
            String bodyImageUrl
    ) {
        public static PreferenceResponse from(User user) {
            return new PreferenceResponse(
                    user.getHeight(),
                    user.getWeight(),
                    user.getGender(),
                    user.getBodyImageUrl()
            );
        }
    }

    public record GetMyPageResponse(

            Long userId,
            String nickname,
            String profileImageUrl,
            String email,
            Rank rank,
            String rankName,
            OauthProvider oauthProvider
    ) {
        public static GetMyPageResponse from(User user) {
            return new GetMyPageResponse(
                    user.getId(),
                    user.getNickname(),
                    user.getEmail(),
                    user.getProfileImageUrl(),
                    user.getRank(),
                    user.getRank().getName(),
                    user.getOauthProvider()
            );
        }
    }
}
