package ru.yandex.practicum.catsgram.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Comment {
    private long id;
    private Post post;
    private User author;
    private String text;
    private Instant creationDate;
}