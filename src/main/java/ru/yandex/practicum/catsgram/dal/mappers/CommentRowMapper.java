package ru.yandex.practicum.catsgram.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.catsgram.model.Comment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Component
public class CommentRowMapper implements RowMapper<Comment> {
    @Override
    public Comment mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Comment comment = new Comment();
        comment.setId(resultSet.getLong("id"));
        comment.setText(resultSet.getString("text"));
        comment.setCreationDate(((Instant) resultSet.getObject("creation_date")));

        return comment;
    }
}
