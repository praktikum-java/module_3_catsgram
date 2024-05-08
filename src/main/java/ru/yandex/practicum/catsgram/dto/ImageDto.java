package ru.yandex.practicum.catsgram.dto;

import lombok.Data;

@Data
public class ImageDto {
    private long id;
    private long postId;
    private String fileName;
    private byte[] data;
}