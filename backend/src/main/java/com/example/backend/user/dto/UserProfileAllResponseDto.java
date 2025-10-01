package com.example.backend.user.dto;

import com.example.backend.user.entity.User;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserProfileAllResponseDto {
    private final Long userId;
    private final String userName;
    private final String email;
    private final String phoneNumber;
    private final String address;
    private final LocalDate birthDate;
    private final String imageUrl;
    private final String backGroundImageUrl;

    public UserProfileAllResponseDto(User user) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.birthDate = user.getBirthDate();
        this.imageUrl = user.getImageUrl();
        this.backGroundImageUrl = user.getBackGroundImageUrl();
    }
}