package com.nova.yeobaek.global.auth.controller.docs;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.domain.enums.OauthProvider;
import com.nova.yeobaek.global.auth.dto.request.RequestDTO;
import com.nova.yeobaek.global.auth.dto.response.ResponseDTO;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증/인가 API (앱 전용)")
public interface AuthControllerDocs {

    @PostMapping("/social/login")
    @Operation(
            summary = "소셜 로그인 API",
            description = """
            앱에서 구글 또는 카카오 SDK 로그인을 수행한 뒤,
            발급받은 토큰을 서버로 전달하여 로그인/회원가입을 처리합니다.

            - 서버는 토큰을 직접 검증합니다.
            - 로그인 성공 시 AccessToken, RefreshToken을 반환합니다.
            """
    )
    CommonResponse<?> socialLogin(
            @RequestBody @Valid RequestDTO.SocialLoginRequest request
    );

    @PostMapping("/api/auth/dev-login")
    @Operation(
            summary = "개발용 로그인 API",
            description = """
            provider : google / kakao (대소문자 상관없음)
   
            """
    )
    CommonResponse<ResponseDTO.LoginResponse> devLogin(
            @RequestParam OauthProvider provider,
            @RequestParam String oauthId
    );

    @PatchMapping("/signup")
    @Operation(
            summary = "닉네임 설정 API",
            description = """
            소셜 로그인 이후 최초 1회 닉네임을 설정합니다.

            - Authorization 헤더(JWT)가 필요합니다.
            - 로그인한 사용자 본인만 변경할 수 있습니다.
            """
    )
    CommonResponse<?> signup(
            @AuthenticationPrincipal(expression = "user") User user,
            @RequestBody @Valid RequestDTO.SignupRequest nickname
    );

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃 API",
            description = """
            현재 로그인한 사용자를 로그아웃 처리합니다.

            - Authorization 헤더의 AccessToken을 블랙리스트 처리합니다.
            - 서버에 저장된 RefreshToken을 삭제합니다.
            - 클라이언트는 토큰을 직접 제거해야 합니다.
            """
    )
    CommonResponse<Void> logout(
            @RequestHeader("Authorization") String authorization,
            @AuthenticationPrincipal(expression = "user") User user
    );

    @PostMapping("/reissue")
    @Operation(
            summary = "Access Token 재발급 API",
            description = """
            Refresh Token을 이용해 AccessToken과 RefreshToken을 재발급합니다.

            - Refresh Token은 요청 Body로 전달합니다.
            - 기존 Refresh Token은 폐기됩니다.
            """
    )
    CommonResponse<?> reissue(
            @RequestBody @Valid RequestDTO.ReissueRequest request
    );
}
