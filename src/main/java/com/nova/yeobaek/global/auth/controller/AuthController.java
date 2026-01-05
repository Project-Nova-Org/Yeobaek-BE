package com.nova.yeobaek.global.auth.controller;

import com.nova.yeobaek.domain.user.domain.User;
import com.nova.yeobaek.domain.user.service.UserService;
import com.nova.yeobaek.global.auth.dto.request.RequestDTO;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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

	// 구글 로그인
	@Override
	@GetMapping("/login/google")
	public void googleLoginInfo(){}

	// 카카오 로그인
	@Override
	@GetMapping("/login/kakao")
	public void kakaoLoginInfo(){}

	// 닉네임 설정
	@Override
	@PatchMapping("/signup")
	public CommonResponse<?> signup(
			@AuthenticationPrincipal(expression = "user") User user,
			@RequestBody @Valid RequestDTO.SignupRequest nickname
	) {
		Long userId = user.getId();
		userService.setNickname(userId,nickname);
		return CommonResponse.onSuccess(nickname);
	}

	// 로그아웃
	@Override
	@PostMapping("/logout")
	public CommonResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		authService.logout(request,response);
		return CommonResponse.onSuccess(null);
	}

	// AccessToken 재발급
	@Override
	@PostMapping("/reissue")
	public CommonResponse<?> reissue(HttpServletRequest request, HttpServletResponse response){
		authService.reissue(request,response);
		return CommonResponse.onSuccess(null);
	}
}
