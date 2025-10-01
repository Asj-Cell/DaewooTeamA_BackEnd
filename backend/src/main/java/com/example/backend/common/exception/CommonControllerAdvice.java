//package com.example.backend.common.exception;
//
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
//@RestControllerAdvice
//public class CommonControllerAdvice implements ResponseBodyAdvice<Object> {
//
//    @Override
//    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//        return true;
//    }
//
//    @Override
//    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
//                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
//                                  ServerHttpResponse response) {
//
//        // Already wrapped → skip
//        if (body instanceof ApiResponse2)
//            return body;
//
//        // Handle String specially (Spring uses StringHttpMessageConverter)
//        if (body instanceof String) {
//            try {
//                return new ObjectMapper().writeValueAsString(ApiResponse2.success(body));
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // Default: wrap in ApiResponse2
//        return ApiResponse2.success(body);
//    }
//
//}
