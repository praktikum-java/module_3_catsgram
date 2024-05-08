package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dto.ImageDto;
import ru.yandex.practicum.catsgram.dto.ImageUploadResponse;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.mapper.ImageMapper;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.Post;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final PostRepository postRepository;

    @Value("${catsgram.image-directory}")
    private String imageDirectory;

    public List<ImageUploadResponse> saveImages(long postId, List<MultipartFile> files) {
        return files.stream().map(file -> saveImage(postId, file)).collect(Collectors.toList());
    }

    public ImageUploadResponse saveImage(long postId, MultipartFile file) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ConditionsNotMetException("Указанный пост не найден"));

        // Сохраняем изображение на диск и возвращаем путь к файлу
        Path filePath = saveFile(file, post);

        Image image = imageRepository.save(ImageMapper.mapToImage(postId, filePath, file.getOriginalFilename()));

        return ImageMapper.mapToImageUploadResponse(image);
    }

    public List<ImageDto> getImages(long postId) {
        return imageRepository.findByPostId(postId)
                .stream()
                .map(image -> {
                    byte[] bytes = loadFile(image);
                    return ImageMapper.mapToImageDto(image, bytes);
                }).collect(Collectors.toList());

    }

    private Path saveFile(MultipartFile file, Post post) {
        try {
            String uniqueFileName = String.format("%d_%d_%s.%s", post.getAuthor().getId(), post.getId(),
                    UUID.randomUUID(), StringUtils.getFilenameExtension(file.getOriginalFilename()));

            Path uploadPath = Path.of(imageDirectory);
            Path filePath = uploadPath.resolve(uniqueFileName);

            // Создаём директории, если они еще не созданы
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Сохраняем файл по сформированному пути
            file.transferTo(filePath);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] loadFile(Image image) {
        Path path = Paths.get(image.getFilePath());
        if (Files.exists(path)) {
            try {
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new ImageFileException("Ошибка чтения файла.  Id: " + image.getId()
                        + ", name: " + image.getOriginalFileName(), e);
            }
        } else {
            throw new ImageFileException("Файл не найден. Id: " + image.getId()
                    + ", name: " + image.getOriginalFileName());
        }
    }
}
