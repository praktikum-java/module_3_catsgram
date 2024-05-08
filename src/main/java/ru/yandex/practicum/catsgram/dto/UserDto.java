package ru.yandex.practicum.catsgram.dto;

import lombok.Data;
import ru.yandex.practicum.catsgram.model.UserProfile;

import java.time.Instant;
import java.util.List;

@Data
public class UserDto {
    private long id;
    private String username;
    private String email;
    private Instant registrationDate;
    private UserProfile profile;
    private List<UserDto> followers;
    private List<UserDto> following;
}
