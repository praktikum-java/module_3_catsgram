package ru.yandex.practicum.catsgram.dto;

import java.time.Instant;

public class CommentDto {
    private long id;
    private CommentatorDto author;
    private String text;
    private Instant creationDate;
}
