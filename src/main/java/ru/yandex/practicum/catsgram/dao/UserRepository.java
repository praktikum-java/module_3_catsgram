package ru.yandex.practicum.catsgram.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public User save(User user) {
        String sql = "INSERT INTO users(username, email, password, registration_date) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setObject(4, Timestamp.from(user.getRegistrationDate()));

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Создать пользователя не удалось.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Создать пользователя не удалось. Идентификатор не получен.");
                }
            }
        } catch (SQLException e) {
            handleSQLException("Сохранение нового пользователя", sql, e);
        }
        return user;
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(mapUser(resultSet));
                }
            }
        } catch (SQLException e) {
            handleSQLException("Выборка всех пользователей", sql, e);
        }
        return result;
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        } catch (SQLException e) {
            handleSQLException("Выборка пользователя с email = " + email, sql, e);
        }
        return Optional.empty();
    }

    public Optional<User> findById(long userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        } catch (SQLException e) {
            handleSQLException("Выборка пользователя с id = " + userId, sql, e);
        }
        return Optional.empty();
    }

    public User update(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setLong(6, user.getId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Обновить пользователя не удалось.");
            }
        } catch (SQLException e) {
            handleSQLException("Обновление пользователя с id = " + user.getId(), sql, e);
        }
        return user;
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));

        Timestamp registrationDate = resultSet.getTimestamp("registration_date");
        user.setRegistrationDate(registrationDate.toInstant());

        return user;
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
