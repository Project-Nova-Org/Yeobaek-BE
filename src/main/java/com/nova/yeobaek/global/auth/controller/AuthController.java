package com.nova.yeobaek.global.auth.controller;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.enums.OauthProvider;
import com.nova.yeobaek.domain.user.service.UserService;
import com.nova.yeobaek.global.auth.dto.request.RequestDTO;
import com.nova.yeobaek.global.auth.dto.response.ResponseDTO;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.nova.yeobaek.global.auth.controller.docs.AuthControllerDocs;
import com.nova.yeobaek.global.auth.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final UserService userService;

    // 소셜로그인
    @Override
    @PostMapping("/social/login")
    public CommonResponse<?> socialLogin(
            @RequestBody @Valid RequestDTO.SocialLoginRequest request
    ) {
        return CommonResponse.onSuccess(authService.socialLogin(request));
    }

    // 개발용 로그인 삭제 예정
    @Override
    @Profile({"local","dev"})
    @PostMapping("/dev-login")
    public CommonResponse<ResponseDTO.LoginResponse> devLogin(
            @RequestParam OauthProvider provider,
            @RequestParam String oauthId
    ) {
        return CommonResponse.onSuccess(authService.devLogin(provider, oauthId));
    }

    // 닉네임 설정
    @Override
    @PatchMapping("/signup")
    public CommonResponse<?> signup(
            @AuthenticationPrincipal(expression = "user") User user,
            @RequestBody @Valid RequestDTO.SignupRequest nickname
    ) {
        Long userId = user.getId();
        userService.setNickname(userId, nickname);
        return CommonResponse.onSuccess(nickname);
    }

    // 로그아웃
    @Override
    @PostMapping("/logout")
    public CommonResponse<Void> logout(
            @RequestHeader("Authorization") String authorization,
            @AuthenticationPrincipal(expression = "user") User user
    ) {
        String accessToken = authorization.replace("Bearer ", "");
        authService.logout(accessToken, user.getId());
        return CommonResponse.onSuccess(null);
    }

    // AccessToken 재발급
    @Override
    @PostMapping("/reissue")
    public CommonResponse<?> reissue(
            @RequestBody @Valid RequestDTO.ReissueRequest request
    ) {
        return CommonResponse.onSuccess(
                authService.reissue(request.refreshToken())
        );
    }
}
