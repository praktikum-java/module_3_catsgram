package ru.yandex.practicum.catsgram.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Image;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ImageRepository {
    private final DataSource dataSource;

    @Autowired
    public ImageRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Image save(Image image) {
        String sql = "INSERT INTO image_storage(original_file_name, file_path, post_id) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, image.getOriginalFileName());
            statement.setString(2, image.getFilePath());
            statement.setLong(3, image.getPostId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Создание изображения не удалось, строки не затронуты.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    image.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Создание изображения не удалось, идентификатор не получен.");
                }
            }
        } catch (SQLException e) {
            handleSQLException("Сохранение нового изображения", sql, e);
        }
        return image;
    }

    public Image findById(long imageId) {
        String sql = "SELECT * FROM image_storage WHERE id = ?";
        Image image = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, imageId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    image = mapImage(resultSet);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Выборка изображения с id = " + imageId, sql, e);
        }
        return image;
    }

    public List<Image> findByPostId(long postId) {
        String sql = "SELECT * FROM image_storage where post_id = ?";
        List<Image> images = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, postId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Image image = mapImage(resultSet);
                    images.add(image);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Выборка изображения поста с id = " + postId, sql, e);
        }
        return images;
    }

    public boolean delete(long imageId) {
        String sql = "DELETE FROM image_storage WHERE id = ?";
        int rowsDeleted = 0;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, imageId);
            rowsDeleted = statement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException("Удаление изображения с id = " + imageId, sql, e);
        }
        return rowsDeleted > 0;
    }

    private Image mapImage(ResultSet resultSet) throws SQLException {
        Image image = new Image();
        image.setId(resultSet.getLong("id"));
        image.setOriginalFileName(resultSet.getString("original_file_name"));
        image.setFilePath(resultSet.getString("file_path"));
        image.setPostId(resultSet.getLong("post_id"));
        return image;
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
