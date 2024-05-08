package ru.yandex.practicum.catsgram.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class UserDto {
    private long id;
    private String username;
    private String email;
    private Instant registrationDate;
}
