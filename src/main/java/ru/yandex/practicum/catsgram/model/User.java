package ru.yandex.practicum.catsgram.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.List;

@Data
@EqualsAndHashCode(of = { "email" })
public class User {
    private long id;
    private String username;
    private String email;
    private String password;
    private Instant registrationDate;
    private UserProfile profile;
    private List<User> followers;
    private List<User> following;
}