package com.example.backend.auth.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.oauth2.authorized-redirect-uri:http://localhost:3000/oauth2/redirect}")
    private String authorizedRedirectUri;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        log.error("=== OAuth2 로그인 실패 ===");
        log.error("Exception type: {}", exception.getClass().getName());
        log.error("Exception message: {}", exception.getMessage());

        // 상세 에러 정보 추출
        String errorCode = "oauth2_error";
        String errorMessage = "로그인 처리 중 오류가 발생했습니다.";

        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oauthEx = (OAuth2AuthenticationException) exception;
            OAuth2Error error = oauthEx.getError();

            if (error != null) {
                errorCode = error.getErrorCode();
                if (error.getDescription() != null) {
                    errorMessage = error.getDescription();
                }
                log.error("OAuth2 Error Code: {}", errorCode);
                log.error("OAuth2 Error Description: {}", error.getDescription());
            }
        }

        // 원인 예외 로깅
        if (exception.getCause() != null) {
            log.error("Caused by: {}", exception.getCause().getMessage(), exception.getCause());
        }

        // 에러 페이지로 리다이렉트
        String targetUrl = buildErrorUrl(errorCode, errorMessage);

        log.info("에러 페이지로 리다이렉트: {}", targetUrl);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * 에러 정보를 포함한 리다이렉트 URL 생성
     */
    private String buildErrorUrl(String errorCode, String errorMessage) {
        try {
            // URL 인코딩
            String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

            return UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                    .queryParam("error", errorCode)
                    .queryParam("message", encodedMessage)
                    .build()
                    .toUriString();

        } catch (Exception e) {
            log.error("에러 URL 생성 중 오류 발생", e);
            // 폴백: 기본 에러 메시지만 포함
            return UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                    .queryParam("error", "unknown_error")
                    .build()
                    .toUriString();
        }
    }
}