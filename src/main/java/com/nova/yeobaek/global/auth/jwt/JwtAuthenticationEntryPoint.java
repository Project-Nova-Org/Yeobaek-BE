package com.nova.yeobaek.global.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
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

        Exception jwtException =
                (Exception) request.getAttribute("exception");

        response.setContentType("application/json;charset=UTF-8");

        if (jwtException instanceof ExpiredJwtException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    objectMapper.writeValueAsString(
                            CommonResponse.onFailure(_UNAUTHORIZED, "Access Token이 만료되었습니다.")
                    )
            );
            return;
        }

        if (jwtException instanceof JwtException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    objectMapper.writeValueAsString(
                            CommonResponse.onFailure(_UNAUTHORIZED, "유효하지 않은 토큰입니다.")
                    )
            );
            return;
        }

        // 기본 인증 실패
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(CommonResponse.onFailure(_UNAUTHORIZED, "로그인 후에 이용해주세요."))
        );
    }
}
