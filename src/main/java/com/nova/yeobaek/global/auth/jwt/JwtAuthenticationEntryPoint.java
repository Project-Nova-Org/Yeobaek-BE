package com.nova.yeobaek.global.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.nova.yeobaek.global.payload.status.CommonErrorStatus._UNAUTHORIZED;

// JWT 인증이 실패했을 때(로그인 안 됨, 토큰 없음/만료 등) response
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(
                objectMapper.writeValueAsString(CommonResponse.onFailure(_UNAUTHORIZED, "로그인 후에 이용해주세요."))
        );
    }
}
