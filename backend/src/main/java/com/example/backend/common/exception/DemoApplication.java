package com.example.backend.common.exception;

import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @RestController
    class Test {

        @GetMapping("/test")
        public String test() throws CommonExceptionTemplate {
            throw MemberException.NOT_EXIST_MEMBER_ID.getException();
            // return null;
        }

    }
}
