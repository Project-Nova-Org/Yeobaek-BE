package com.nova.yeobaek.domain.user.controller.docs;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.dto.request.UserRequestDTO;
import com.nova.yeobaek.domain.user.dto.response.UserResponseDTO;
import com.nova.yeobaek.global.auth.dto.request.RequestDTO;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User", description = "사용자 API")
public interface UserControllerDocs {

    @PatchMapping("/nickname")
    @Operation(
            summary = "닉네임 변경 API",
            description = """
            닉네임을 변경합니다.

            - Authorization 헤더(JWT)가 필요합니다.
            - 로그인한 사용자 본인만 변경할 수 있습니다.
            """
    )
    CommonResponse<?> setNickname(
            @AuthenticationPrincipal(expression = "user") User user,
            @RequestBody @Valid RequestDTO.SignupRequest nickname
    );


    @PatchMapping("/preferences")
    @Operation(
            summary = "맞춤정보 설정 API",
            description = """
            맞춤정보를 설정합니다.

            - Authorization 헤더(JWT)가 필요합니다.
            - 로그인한 사용자 본인만 변경할 수 있습니다.
            키, 몸무게, 전신사진 이미지, 성별을 선택할 수 있습니다.
            """
    )
    CommonResponse<?> setPreference(
            @AuthenticationPrincipal(expression = "user") User user,
            @RequestBody @Valid UserRequestDTO.PreferenceRequest request
    );

    @Operation(
            summary = "회원 탈퇴 API",
            description = """
            회원탈퇴합니다.

            - Authorization 헤더(JWT)가 필요합니다.
            - SOFT DELETE로 진행되며 30일후에 Hard delete 됩니다.
            """
    )
    @DeleteMapping("/me")
    CommonResponse<Void> withdraw(
            @AuthenticationPrincipal(expression = "user") User user
    );

    @Operation(
            summary = "마이페이지 조회 API",
            description = """
            마이페이지를 조회합니다.

            - Authorization 헤더(JWT)가 필요합니다.
            - 사용자 등급에 해당하는 로고를 보여줍니다.
            - 단계별 별명,닉네임, 소셜 계정(이메일)을 보여줍니다.
            """
    )
    @GetMapping("/me")
    CommonResponse<UserResponseDTO.GetMyPageResponse> getMypage(
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
