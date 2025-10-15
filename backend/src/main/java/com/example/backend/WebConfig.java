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

                        // 배포 서버용 주소
                        "http://49.247.160.225",
                        "https://49.247.160.225",

                        "http://13.125.235.75",
                        "https://13.125.235.75",


                        //토스페이
                        "117.52.3.202",
                        "117.52.3.210",
                        "211.115.96.202",
                        "211.115.96.210",
                        "106.249.5.202",
                        "106.249.5.210"
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