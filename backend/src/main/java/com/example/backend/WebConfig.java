package com.example.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        // 기존 로컬 개발용 주소

                        "http://localhost", "http://localhost:80",
                        "http://127.0.0.1", "http://127.0.0.1:80",

                        "http://localhost:8080",
                        // 배포 서버용 주소
                        "http://49.247.160.225",
                        "https://49.247.160.225",

                        "http://13.125.235.75",
                        "https://13.125.235.75"

//                        곧 없앨확률 높음 에러가 없다면
//                        //토스페이
//                        "http://117.52.3.202",
//                        "http://117.52.3.210",
//                        "http://211.115.96.202",
//                        "http://211.115.96.210",
//                        "http://106.249.5.202",
//                        "http://106.249.5.210"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:uploads/");
    }
}