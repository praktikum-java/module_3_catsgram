package ru.yandex.practicum.catsgram.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.catsgram.exception.InternalServerException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcClient jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        return jdbc.sql(query)
                .params(params)
                .query(mapper)
                .optional();
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.sql(query)
                .params(params)
                .query(mapper)
                .list();
    }

    public boolean delete(long id, String query) {
        int rowsDeleted = jdbc.sql(query)
                .param(id)
                .update();
        return rowsDeleted > 0;
    }

    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.sql(query)
                .params(params)
                .update(keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.sql(query)
                .params(params)
                .update();
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }
}
