package ru.yandex.practicum.catsgram.dto;

import lombok.Data;

@Data
public class PostAuthorDto {
    private long id;
    private String username;
    private String email;
}
