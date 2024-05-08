package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class PostRepository extends BaseRepository<Post> {
    private static final String INSERT_QUERY = "INSERT INTO posts(author_id, description, post_date) " +
            "VALUES (?, ?, ?) returning id";
    private static final String FIND_BY_ID_QUERY  = "SELECT * FROM posts WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE posts SET description = ?, post_date = ? WHERE id = ?";

    public PostRepository(JdbcTemplate jdbc, RowMapper<Post> mapper) {
        super(jdbc, mapper, Post.class);
    }

    public Post save(Post post) {
        long id = insert(
                INSERT_QUERY,
                post.getAuthor().getId(),
                post.getDescription(),
                Timestamp.from(post.getPostDate())
        );
        post.setId(id);
        return post;
    }

    public Optional<Post> findById(long postId) {
        return findOne(FIND_BY_ID_QUERY, postId);
    }

    public Post update(Post post) {
        update(
                UPDATE_QUERY,
                post.getDescription(),
                Timestamp.from(post.getPostDate()),
                post.getId()
        );
        return post;
    }
}
