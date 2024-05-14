package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dal.UserRepository;
import ru.yandex.practicum.catsgram.dto.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.dto.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.PostMapper;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    public PostDto createPost(NewPostRequest newPostRequest) {
        User author = userRepository.findById(newPostRequest.getAuthorId())
                .orElseThrow(() -> new ConditionsNotMetException("Указанный автор не найден"));

        Post post = PostMapper.mapToPost(newPostRequest, author);

        postRepository.save(post);

        return PostMapper.mapToPostDto(post);
    }

    public PostDto getPostById(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Пост с идентификатором " + postId + " не найден."));

        User author = userRepository.findById(post.getAuthor().getId())
                .orElseThrow(() -> new RuntimeException("Автор поста не найден"));

        List<Image> images = imageRepository.findByPostId(postId);

        post.setAuthor(author);
        post.setImages(images);

        return PostMapper.mapToPostDto(post);
    }

    public PostDto updatePost(long postId, UpdatePostRequest request) {
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Текст публикации не может быть пустым");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Пост с идентификатором " + postId + " не найден."));

        post.setDescription(request.getDescription());
        post.setPostDate(Instant.now());

        postRepository.update(post);

        return PostMapper.mapToPostDto(post);
    }
}
