package ru.yandex.practicum.catsgram.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.PostRowMapper;
import ru.yandex.practicum.catsgram.exception.InternalServerException;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final JdbcClient jdbc;
    private final PostRowMapper mapper;

    public Post save(Post post) {
        String query = "INSERT INTO posts(author_id, description, post_date) " +
                "VALUES (?, ?, ?) returning id";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.sql(query)
                .param(post.getAuthor().getId())
                .param(post.getDescription())
                .param(Timestamp.from(post.getPostDate()))
                .update(keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            post.setId(id);
        } else {
            throw new InternalServerException("Создать пост не удалось, идентификатор не получен.");
        }

        return post;
    }

    public Optional<Post> findById(long postId) {
        String query = "SELECT * FROM posts WHERE id = ?";
        return jdbc.sql(query)
                .param(postId)
                .query(mapper)
                .optional();
    }

    public Post update(Post post) {
        String query = "UPDATE posts SET description = ?, post_date = ? WHERE id = ?";
        int rowsUpdated = jdbc.sql(query)
                .param(post.getDescription())
                .param(post.getPostDate())
                .param(post.getId())
                .update();
        if (rowsUpdated == 0) {
            throw new InternalServerException("Обновить пост не удалось.");
        }
        return post;
    }
}
