package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class PostRepository extends BaseRepository<Post> {

    public PostRepository(JdbcClient jdbc, RowMapper<Post> mapper) {
        super(jdbc, mapper);
    }

    public Post save(Post post) {
        String query = "INSERT INTO posts(author_id, description, post_date) " +
                "VALUES (?, ?, ?) returning id";
        long id = insert(
                query,
                post.getAuthor().getId(),
                post.getDescription(),
                Timestamp.from(post.getPostDate())
        );
        post.setId(id);
        return post;
    }

    public Optional<Post> findById(long postId) {
        String query = "SELECT * FROM posts WHERE id = ?";
        return findOne(query, postId);
    }

    public Post update(Post post) {
        String query = "UPDATE posts SET description = ?, post_date = ? WHERE id = ?";
        update(
                query,
                post.getDescription(),
                Timestamp.from(post.getPostDate()),
                post.getId()
        );
        return post;
    }
}
