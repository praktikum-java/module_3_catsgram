package ru.yandex.practicum.catsgram.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class PostDto {
    private long id;
    private PostAuthorDto author;
    private String description;
    private Instant postDate;
    private List<String> photos;
    private List<CommentDto> comments;
}
