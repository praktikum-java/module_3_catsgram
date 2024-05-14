package ru.yandex.practicum.catsgram.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String username;
    private String email;
    private String password;

    public boolean hasUsername() {
        return ! (username == null || username.isBlank());
    }

    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }

    public boolean hasPassword() {
        return ! (password == null || password.isBlank());
    }
}
