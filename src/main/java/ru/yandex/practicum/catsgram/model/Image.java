package ru.yandex.practicum.catsgram.model;

import lombok.Data;

@Data
public class Image {
    private long id;
    private long postId;
    private String originalFileName;
    private String filePath;
}