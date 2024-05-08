package ru.yandex.practicum.catsgram.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.ImageRowMapper;
import ru.yandex.practicum.catsgram.exception.InternalServerException;
import ru.yandex.practicum.catsgram.model.Image;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ImageRepository {
    private final JdbcClient jdbc;
    private final ImageRowMapper mapper;

    public Image save(Image image) {
        String query = "INSERT INTO image_storage(original_file_name, file_path, post_id) " +
                "VALUES (?, ?, ?) returning id";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.sql(query)
                .param(image.getOriginalFileName())
                .param(image.getFilePath())
                .param(image.getPostId())
                .update(keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            image.setId(id);
        } else {
            throw new InternalServerException("Создать пост не удалось, идентификатор не получен.");
        }
        return image;
    }

    public Optional<Image> findById(long imageId) {
        String query = "SELECT * FROM image_storage WHERE id = ?";
        return jdbc.sql(query)
                .param(imageId)
                .query(mapper)
                .optional();
    }

    public List<Image> findByPostId(long postId) {
        String query = "SELECT * FROM image_storage where post_id = ?";
        return jdbc.sql(query)
                .param(postId)
                .query(mapper)
                .list();
    }

    public boolean delete(long imageId) {
        String query = "DELETE FROM image_storage WHERE id = ?";
        int rowsDeleted = jdbc.sql(query)
                .param(imageId)
                .update();
        return rowsDeleted > 0;
    }
}
