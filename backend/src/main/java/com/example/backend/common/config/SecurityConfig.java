package com.example.backend.common.config;

import com.example.backend.auth.oauth.CustomOAuth2UserService;
import com.example.backend.auth.oauth.OAuth2LoginFailureHandler;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final ObjectMapper objectMapper; // ObjectMapper 주입
//        http://localhost:18888/oauth2/authorization/kakao
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 출처(Origin) 목록을 설정합니다.
        configuration.setAllowedOrigins(Arrays.asList(
                // 프론트엔드 개발 서버
                "http://localhost:8080",
                // 기타 로컬 주소
                "http://localhost:18888",
                "http://localhost",
                "http://127.0.0.1",
                // 배포 서버 주소
                "http://49.247.160.225", "https://49.247.160.225",
                "http://13.125.235.75", "https://13.125.235.75"
        ));
        // 허용할 HTTP 메서드를 설정합니다.
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 허용할 헤더를 설정합니다.
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 자격 증명(쿠키, 인증 헤더 등)을 허용합니다.
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위 설정 적용
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Preflight OPTIONS 요청 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. 정적 리소스 허용
                        .requestMatchers(
                                "/favicon.ico",
                                "/error",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/*.js",
                                "/*.css",
                                "/images/**",
                                "/uploads/**"
                        ).permitAll()

                        // 3. API 엔드포인트 허용
                        .requestMatchers(
                                "/api/auth/**",
                                "/login/oauth2/**",
                                "/oauth2/authorization/**",
                                "/api/travel-packages/**",
                                "/api/hotels/filter",
                                "/api/hotels/detail/**",
                                "/api/hotels/*/reviews/**",
                                "/api/pay",
                                "/api/pay/brandpay",
                                "/api/pay/*/cancel"
                        ).permitAll()

                        // 4. HTML 페이지 허용
                        .requestMatchers(
                                "/success.html",
                                "/fail.html",
                                "/check.html"
                        ).permitAll()

                        // 5. 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
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
                            ApiResponse<String> successResponse = ApiResponse.success("성공적으로 로그아웃되었습니다.");
                            objectMapper.writeValue(response.getWriter(), successResponse);
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
