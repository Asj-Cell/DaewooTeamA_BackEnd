// SwaggerConfig.java
package com.example.backend.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("여행 예약 프로젝트 API 문서")
                .version("v1.0.0")
                .description("여행 예약 프로젝트의 API 명세서입니다.");

        // OAuth2 스키마 설정
        SecurityScheme oauth2SecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                                .authorizationUrl("/oauth2/authorization/google")
                                .tokenUrl("/login/oauth2/code/google")
                                .scopes(new Scopes()
                                        .addString("email", "email")
                                        .addString("profile", "profile"))));

        // JWT 스키마 설정
        SecurityScheme jwtScheme = new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // 보안 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("oauth2", Arrays.asList("email", "profile"))
                .addList("bearerAuth");

        Components components = new Components()
                .addSecuritySchemes("oauth2", oauth2SecurityScheme)
                .addSecuritySchemes("bearerAuth", jwtScheme);

        return new OpenAPI()
                .info(info)
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}