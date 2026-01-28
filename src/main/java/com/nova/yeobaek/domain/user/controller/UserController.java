package com.nova.yeobaek.domain.user.controller;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.dto.request.UserRequestDTO;
import com.nova.yeobaek.domain.user.dto.response.UserResponseDTO;
import com.nova.yeobaek.global.auth.dto.request.RequestDTO;
import com.nova.yeobaek.global.auth.security.CustomUserDetails;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.nova.yeobaek.domain.user.controller.docs.UserControllerDocs;
import com.nova.yeobaek.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @Override
    @PatchMapping("/nickname")
    public CommonResponse<?> setNickname(
            @AuthenticationPrincipal(expression = "user") User user,
            @RequestBody @Valid RequestDTO.SignupRequest request
    ) {
        Long userId = user.getId();
        userService.setNickname(userId, request);
        return CommonResponse.onSuccess(request.newNickname());
    }

    @Override
    @PatchMapping("/preferences")
    public CommonResponse<UserResponseDTO.PreferenceResponse> setPreference(
            @AuthenticationPrincipal(expression = "user") User user,
            @RequestBody @Valid UserRequestDTO.PreferenceRequest request
    ) {
        Long userId = user.getId();
        UserResponseDTO.PreferenceResponse response =
                userService.setPreference(userId, request);

        return CommonResponse.onSuccess(response);
    }

    @Override
    @DeleteMapping
    public CommonResponse<UserResponseDTO.WithDrawResponse> withdraw(
            @AuthenticationPrincipal(expression = "user") User user
    ) {
        UserResponseDTO.WithDrawResponse response =
        userService.withdraw(user.getId());
        return CommonResponse.onSuccess(response);
    }

    @Override
    @GetMapping
    public CommonResponse<UserResponseDTO.GetMyPageResponse> getMypage(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserResponseDTO.GetMyPageResponse
                response = userService.getMypage(userDetails.getUser().getId());
        return CommonResponse.onSuccess(response);
    }
}
