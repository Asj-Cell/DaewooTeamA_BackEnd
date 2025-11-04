package com.example.backend.user.dto;

import com.example.backend.user.entity.User;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserProfileResponseDto {

    private final String userName;
    private final String email;
    private final String phoneNumber;
    private final String address;
    private final LocalDate birthDate;
    private final String provider;

    public UserProfileResponseDto(User user) {
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.birthDate = user.getBirthDate();
        this.provider = user.getProvider();
    }
}