package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dao.PostRepository;
import ru.yandex.practicum.catsgram.dao.UserRepository;
import ru.yandex.practicum.catsgram.dto.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.NewPostResponse;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.PostMapper;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public NewPostResponse createPost(NewPostRequest newPostRequest) {
        User author = userRepository.findById(newPostRequest.getAuthorId())
                .orElseThrow(() -> new ConditionsNotMetException("Указанный автор не найден"));

        Post post = PostMapper.mapToPost(newPostRequest, author);

        postRepository.save(post);

        return PostMapper.mapToNewPostResponse(post);
    }

    public PostDto getPostById(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Пост с идентификатором " + postId + " не найден."));

        User author = userRepository.findById(post.getAuthor().getId())
                .orElseThrow(() -> new RuntimeException("Автор поста не найден"));

        post.setAuthor(author);

        return PostMapper.mapToPostDto(post);
    }
}
