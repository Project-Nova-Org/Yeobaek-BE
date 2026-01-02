package com.nova.yeobaek.global.auth.controller.docs;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.global.auth.dto.request.RequestDTO;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증/인가 API")
public interface AuthControllerDocs {

    @GetMapping("/login/google")
    @Operation(
            summary = "구글 로그인 (브라우저 리다이렉트)",
            description = """
    이 엔드포인트는 API 호출용이 아닙니다.

    프론트엔드에서 로그인 버튼 클릭 시
    아래 URL로 페이지 이동(redirect)시키면
    구글 로그인이 시작됩니다.

    [개발 환경]
    http://localhost:8080/oauth2/authorization/google
    [운영 환경]
    
    """
    )
    default void googleLoginInfo(){}

    @GetMapping("/login/kakao")
    @Operation(
            summary = "카카오 로그인 (브라우저 리다이렉트)",
            description = """
    이 엔드포인트는 API 호출용이 아닙니다.

    프론트엔드에서 로그인 버튼 클릭 시
    아래 URL로 페이지 이동(redirect)시키면
    카카오 로그인이 시작됩니다.

    [개발 환경]
    http://localhost:8080/oauth2/authorization/kakao
    [운영 환경]
    
    """
    )
    default void kakaoLoginInfo(){}


    @PatchMapping("/signup")
    @Operation(
            summary = "닉네임설정(회원가입) API",
            description = """
     구글 또는 카카오 소셜로그인을 한 후 닉네임 설정을 합니다.
    """
    )
    CommonResponse<?> signup(@AuthenticationPrincipal(expression = "user") User user,
                             @RequestBody @Valid RequestDTO.SignupRequest nickname);


    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃 API",
            description = """
    클라이언트에 저장된 인증 정보를 제거하여 로그아웃 처리합니다.

    - 로그인 여부와 관계없이 호출할 수 있습니다.
    - Access Token 및 Refresh Token 쿠키를 만료시킵니다.
    - 서버는 로그인 상태를 유지하지 않습니다(JWT 기반).
    """
    )
    CommonResponse<Void> logout(HttpServletResponse response);

    @Operation(
            summary = "Access Token 재발급 API",
            description = """
            만료된 Access Token을 Refresh Token을 이용해 재발급합니다.
            
            - Refresh Token은 HttpOnly Cookie로 전달됩니다.
            - 요청 본문이나 Authorization 헤더는 필요하지 않습니다.
            - Refresh Token이 유효하지 않으면 재발급에 실패합니다.
            """
    )
    @PostMapping("/reissue")
    CommonResponse<?> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    );
}
