package ru.yandex.practicum.catsgram.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.CommentRowMapper;
import ru.yandex.practicum.catsgram.exception.InternalServerException;
import ru.yandex.practicum.catsgram.model.Comment;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
    private final JdbcClient jdbc;
    private final CommentRowMapper mapper;

    public Comment save(Comment comment) {
        String query = "INSERT INTO comments(author_id, post_id, text, creation_date) " +
                "VALUES (?, ?, ?, ?) returning id";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.sql(query)
                .param(comment.getAuthor().getId())
                .param(comment.getPost().getId())
                .param(comment.getText())
                .param(comment.getCreationDate())
                .update(keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            comment.setId(id);
        } else {
            throw new InternalServerException("Создать комментарий не удалось, идентификатор не получен.");
        }

        return comment;
    }

    public Optional<Comment> findById(long commentId) {
        String query = "SELECT * FROM comments WHERE id = ?";
        return jdbc.sql(query)
                .param(commentId)
                .query(mapper)
                .optional();

    }

    public Comment update(Comment comment) {
        String query = "UPDATE comments SET text = ?, creation_date = ? WHERE id = ?";
        int rowsUpdated = jdbc.sql(query)
                .param(comment.getText())
                .param(comment.getCreationDate())
                .param(comment.getId())
                .update();

        if (rowsUpdated == 0) {
            throw new InternalServerException("Обновить комментарий не удалось.");
        }
        return comment;
    }
}
