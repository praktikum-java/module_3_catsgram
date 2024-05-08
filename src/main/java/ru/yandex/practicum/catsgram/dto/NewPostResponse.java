package ru.yandex.practicum.catsgram.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class NewPostResponse {
    private long id;
    private PostAuthorDto author;
    private String description;
    private Instant postDate;
}
