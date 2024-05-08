package ru.yandex.practicum.catsgram.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.UserRowMapper;
import ru.yandex.practicum.catsgram.exception.InternalServerException;
import ru.yandex.practicum.catsgram.model.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcClient jdbc;
    private final UserRowMapper mapper;

    public List<User> findAll() {
        String query = "SELECT * FROM users";
        return jdbc.sql(query)
                .query(mapper)
                .list();
    }

    public Optional<User> findByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        return jdbc.sql(query)
                .param(email)
                .query(mapper)
                .optional();
    }

    public Optional<User> findById(long userId) {
        String query = "SELECT * FROM users WHERE id = ?";
        return jdbc.sql(query)
                .param(userId)
                .query(mapper)
                .optional();
    }

    public User save(User user) {
        String query = "INSERT INTO users(username, email, password, registration_date)" +
                "VALUES (?, ?, ?, ?) returning id";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.sql(query)
                .param(user.getUsername())
                .param(user.getEmail())
                .param(user.getPassword())
                .param(Timestamp.from(user.getRegistrationDate()))
                .update(keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            user.setId(id);
        } else {
            throw new InternalServerException("Создать пользователя не удалось. Идентификатор не получен.");
        }
        return user;
    }

    public User update(User user) {
        String query = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
        int rowsUpdated = jdbc.sql(query)
                .param(user.getUsername())
                .param(user.getEmail())
                .param(user.getPassword())
                .param(user.getId())
                .update();

        if (rowsUpdated == 0) {
            throw new InternalServerException("Обновить пользователя не удалось.");
        }
        return user;
    }
}
