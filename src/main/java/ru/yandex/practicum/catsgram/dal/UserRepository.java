package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {

    public UserRepository(JdbcClient jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        String query = "SELECT * FROM users";
        return findMany(query);
    }

    public Optional<User> findByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        return findOne(query, email);
    }

    public Optional<User> findById(long userId) {
        String query = "SELECT * FROM users WHERE id = ?";
        return findOne(query, userId);
    }

    public User save(User user) {
        String query = "INSERT INTO users(username, email, password, registration_date)" +
                "VALUES (?, ?, ?, ?) returning id";
        long id = insert(
                query,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                Timestamp.from(user.getRegistrationDate())
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        String query = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
        update(
                query,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getId());
        return user;
    }
}
