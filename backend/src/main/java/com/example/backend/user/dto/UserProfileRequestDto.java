package com.example.backend.user.dto;

import com.example.backend.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileRequestDto {
    private Long userId;
    private String userName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate birthDate;

    public UserProfileRequestDto(User user) {
        this.userId = user.getId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.birthDate = user.getBirthDate();
    }
}