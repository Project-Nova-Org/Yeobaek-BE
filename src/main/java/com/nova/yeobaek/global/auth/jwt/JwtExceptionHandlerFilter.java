package com.nova.yeobaek.global.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.yeobaek.global.auth.exception.AuthException;
import com.nova.yeobaek.global.auth.exception.code.AuthErrorStatus;
import com.nova.yeobaek.global.payload.response.CommonResponse;
import com.nova.yeobaek.global.payload.status.ErrorReason;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
        } catch (AuthException e) {
            handleAuthException(response, e);
        }
    }

    private void handleAuthException(
            HttpServletResponse response,
            AuthException e
    ) throws IOException {

        ErrorReason reason = e.getErrorReason();

        if (reason instanceof AuthErrorStatus status) {
            writeError(response, status);
            return;
        }

        writeFallbackError(response, reason);
    }

    private void writeError(
            HttpServletResponse response,
            AuthErrorStatus status
    ) throws IOException {

        response.setStatus(status.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        CommonResponse.onFailure(
                                status,
                                status.getMessage()
                        )
                )
        );
    }

    private void writeFallbackError(
            HttpServletResponse response,
            ErrorReason reason
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(
                objectMapper.writeValueAsString(
                        CommonResponse.onFailure(
                                reason,
                                reason.getMessage()
                        )
                )
        );
    }
}
