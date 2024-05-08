package ru.yandex.practicum.catsgram.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final DataSource dataSource;

    public Post save(Post post) {
        String sql = "INSERT INTO posts(author_id, description, post_date) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setLong(1, post.getAuthor().getId());
            statement.setString(2, post.getDescription());
            statement.setObject(3, Timestamp.from(post.getPostDate()));

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Создать пост не удалось.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Создать пост не удалось, идентификатор не получен.");
                }
            }
        } catch (SQLException e) {
            handleSQLException("Сохранение нового поста", sql, e);
        }

        return post;
    }

    public Optional<Post> findById(long postId) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, postId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapPost(resultSet));
                }
            }
        } catch (SQLException e) {
            handleSQLException("Выборка поста с id = " + postId, sql, e);
        }
        return Optional.empty();
    }

    public Post update(Post post) {
        String sql = "UPDATE posts SET description = ?, post_date = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, post.getDescription());
            statement.setObject(2, post.getPostDate());
            statement.setLong(3, post.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Обновить пост не удалось.");
            }
        } catch (SQLException e) {
            handleSQLException("Обновление поста с id = " + post.getId(), sql, e);
        }
        return post;
    }

    private Post mapPost(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getLong("id"));
        post.setDescription(resultSet.getString("description"));
        post.setPostDate(resultSet.getTimestamp("post_date").toInstant());

        User user = new User();
        user.setId(resultSet.getLong("author_id"));

        post.setAuthor(user);
        return post;
    }

    private void handleSQLException(String task, String sql, SQLException e) {
        SQLErrorCodeSQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
        DataAccessException dae = translator.translate(task, sql, e);
        if (dae != null) {
            throw dae;
        } else {
            throw new UncategorizedSQLException(task, sql, e);
        }
    }
}
