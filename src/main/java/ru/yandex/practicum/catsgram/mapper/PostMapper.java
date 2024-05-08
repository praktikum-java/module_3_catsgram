package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.dto.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.NewPostResponse;
import ru.yandex.practicum.catsgram.dto.PostAuthorDto;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostMapper {

    public static NewPostResponse mapToNewPostResponse(Post post) {
        NewPostResponse response = new NewPostResponse();
        response.setId(post.getId());
        response.setPostDate(post.getPostDate());
        response.setDescription(post.getDescription());
        response.setAuthor(mapToPostAuthorDto(post.getAuthor()));
        return response;
    }

    public static Post mapToPost(NewPostRequest request, User author) {
        Post post = new Post();
        post.setDescription(request.getDescription());
        post.setPostDate(Instant.now());
        post.setAuthor(author);
        return post;
    }

    public static PostDto mapToPostDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setDescription(post.getDescription());

        User author = post.getAuthor();
        dto.setAuthor(mapToPostAuthorDto(author));

        return dto;
    }

    public static PostAuthorDto mapToPostAuthorDto(User author) {
        PostAuthorDto authorDto = new PostAuthorDto();
        authorDto.setId(author.getId());
        authorDto.setEmail(author.getEmail());
        authorDto.setUsername(author.getUsername());
        return authorDto;
    }
}
