package com.example.backend.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // import 추가
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // application.yml에 설정된 username을 주입받습니다.
    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromAddress + "@naver.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}

