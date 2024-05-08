package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.CommentRowMapper;
import ru.yandex.practicum.catsgram.model.Comment;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class CommentRepository extends BaseRepository<Comment> {

    public CommentRepository(JdbcClient jdbc, CommentRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Comment save(Comment comment) {
        String query = "INSERT INTO comments(author_id, post_id, text, creation_date) " +
                "VALUES (?, ?, ?, ?) returning id";
        long id = insert(
                query,
                comment.getAuthor().getId(),
                comment.getPost().getId(),
                comment.getText(),
                Timestamp.from(comment.getCreationDate())
        );
        comment.setId(id);
        return comment;
    }

    public Optional<Comment> findById(long commentId) {
        String query = "SELECT * FROM comments WHERE id = ?";
        return findOne(query, commentId);
    }

    public Comment update(Comment comment) {
        String query = "UPDATE comments SET text = ?, creation_date = ? WHERE id = ?";
        update(
                query,
                comment.getText(),
                Timestamp.from(comment.getCreationDate()),
                comment.getId()
        );
        return comment;
    }
}