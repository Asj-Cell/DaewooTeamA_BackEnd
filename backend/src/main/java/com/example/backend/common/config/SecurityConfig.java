package com.example.backend.common.config;

import com.example.backend.auth.oauth.CustomOAuth2UserService;
import com.example.backend.auth.oauth.OAuth2LoginSuccessHandler;
import com.example.backend.common.util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final ObjectMapper objectMapper; // ObjectMapper 주입

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS 설정 활성화
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 2. Preflight OPTIONS 요청은 인증 없이 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotels/*/reviews/**").permitAll()
                        .requestMatchers(
                                // 일반 인증 관련 public 엔드포인트
                                "/api/auth/**", // /api/auth/logout도 포함되므로 명시적으로 분리할 필요 없음
                                // 소셜 로그인 관련 public 엔드포인트
                                "/login/oauth2/**",
                                // Swagger 및 기타 public 엔드포인트
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/travel-packages/**",
                                "/api/hotels/filter",
                                "/api/hotels/detail/**",
                                "/uploads/**", // 업로드된 이미지 경로 허용
                                "/images/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

                            // ApiResponse 객체를 사용하여 성공 응답 생성
                            ApiResponse<String> successResponse = ApiResponse.success("성공적으로 로그아웃되었습니다.");

                            // ObjectMapper를 사용하여 JSON 문자열로 변환 후 응답 본문에 작성
                            objectMapper.writeValue(response.getWriter(), successResponse);
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
