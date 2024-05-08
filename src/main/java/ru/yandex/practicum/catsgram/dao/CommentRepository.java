package ru.yandex.practicum.catsgram.dao;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Comment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Repository
public class CommentRepository {
    private final DataSource dataSource;

    public CommentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments(author_id, post_id, text, creation_date) VALUES (?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setLong(1, comment.getAuthor().getId());
            statement.setLong(2, comment.getPost().getId());
            statement.setString(3, comment.getText());
            statement.setObject(4, comment.getCreationDate());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating comment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    comment.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating comment failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return comment;
    }

    public Comment findById(long commentId) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, commentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapComment(resultSet);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return null;
    }

    public Comment update(Comment comment) {
        String sql = "UPDATE comments SET text = ?, creation_date = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, comment.getText());
            statement.setObject(2, comment.getCreationDate());
            statement.setLong(3, comment.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Updating comment failed, no rows affected.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return comment;
    }

    private Comment mapComment(ResultSet resultSet) throws SQLException {
        Comment comment = new Comment();
        comment.setId(resultSet.getLong("id"));
        comment.setText(resultSet.getString("text"));
        comment.setCreationDate(((Instant) resultSet.getObject("creation_date")));

        return comment;
    }

    private void handleSQLException(SQLException e) {
        // Обработка исключения
        e.printStackTrace();
    }
}
