package com.example.backend.auth.oauth;

import com.example.backend.common.util.JwtUtil;
import com.example.backend.user.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    // 기본값 설정 추가
    @Value("${app.oauth2.authorized-redirect-uri:http://localhost:80/oauth2/redirect}")
    private String authorizedRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        try {
            log.info("=== OAuth2 로그인 성공 처리 시작 ===");
            log.debug("Authentication type: {}", authentication.getClass().getName());

            // 1. CustomOAuth2User에서 User 객체 추출
            if (!(authentication.getPrincipal() instanceof CustomOAuth2User)) {
                log.error("Principal이 CustomOAuth2User 타입이 아닙니다: {}",
                        authentication.getPrincipal().getClass().getName());
                throw new IllegalStateException("Invalid principal type");
            }

            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            User user = oAuth2User.getUser();

            if (user == null || user.getId() == null) {
                log.error("User 객체 또는 User ID가 null입니다");
                throw new IllegalStateException("User information is missing");
            }

            log.info("사용자 인증 완료 - userId: {}, email: {}, provider: {}",
                    user.getId(), user.getEmail(), user.getProvider());

            // 2. JWT 토큰 생성
            String token = jwtUtil.generateToken(user.getId());
            log.info("JWT 토큰 생성 완료 - userId: {}", user.getId());
            log.debug("Generated token (first 20 chars): {}...",
                    token.length() > 20 ? token.substring(0, 20) : token);

            // 3. 리다이렉트 URL 생성
            String targetUrl = buildTargetUrl(token);
            log.info("리다이렉트 URL 생성 완료: {}", targetUrl);

            // 4. 캐시 방지 헤더 설정
            clearAuthenticationAttributes(request);
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            // 5. 프론트엔드로 리다이렉트
            log.info("프론트엔드로 리다이렉트 실행");
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

            log.info("=== OAuth2 로그인 성공 처리 완료 ===");

        } catch (IllegalStateException e) {
            log.error("OAuth2 로그인 성공 처리 중 상태 오류 발생: {}", e.getMessage(), e);
            handleError(response, "로그인 처리 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("OAuth2 로그인 성공 처리 중 예상치 못한 오류 발생", e);
            handleError(response, "로그인 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 토큰을 포함한 타겟 URL 생성
     */
    private String buildTargetUrl(String token) {
        try {
            String url = UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                    .queryParam("token", token)
                    .build()
                    .toUriString();

            log.debug("Built target URL: {}", url);
            return url;

        } catch (Exception e) {
            log.error("리다이렉트 URL 생성 중 오류 발생 - redirectUri: {}",
                    authorizedRedirectUri, e);
            throw new IllegalStateException("Failed to build redirect URL", e);
        }
    }

    /**
     * 에러 발생 시 에러 페이지로 리다이렉트
     */
    private void handleError(HttpServletResponse response, String errorMessage) {
        try {
            String errorUrl = UriComponentsBuilder.fromUriString(authorizedRedirectUri)
                    .queryParam("error", errorMessage)
                    .build()
                    .toUriString();

            log.info("에러 페이지로 리다이렉트: {}", errorUrl);
            response.sendRedirect(errorUrl);

        } catch (IOException e) {
            log.error("에러 리다이렉트 실패", e);
        }
    }
}