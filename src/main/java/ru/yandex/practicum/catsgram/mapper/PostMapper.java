package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.dto.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostMapper {

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
        dto.setPostDate(post.getPostDate());

        User author = post.getAuthor();
        dto.setAuthor(UserMapper.mapToUserDto(author));

        if (Objects.nonNull(post.getImages())) {
            List<Long> imageIds = post.getImages()
                    .stream()
                    .map(Image::getId)
                    .collect(Collectors.toList());
            dto.setImages(imageIds);
        }

        return dto;
    }
}
