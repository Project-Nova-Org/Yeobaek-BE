package com.nova.yeobaek.global.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.nova.yeobaek.global.payload.status.CommonErrorStatus._UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            writeUnauthorized(response, "Access Token이 만료되었습니다.");
            return;

        } catch (JwtException | IllegalArgumentException e) {
            writeUnauthorized(response, "유효하지 않은 토큰입니다.");
            return;
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        CommonResponse.onFailure(
                                _UNAUTHORIZED,
                                message
                        )
                )
        );
    }
}

