package ru.yandex.practicum.filmorate.storage.DirectorDaoStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMapper rowMapper;

    @Override
    public Director create(Director director) {
        int directorId = 0;
        String directorName = director.getName();

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", directorName);

        directorId = simpleJdbcInsert.executeAndReturnKey(params).intValue();

        return get(directorId);
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "UPDATE DIRECTORS SET NAME = ? WHERE ID = ?";
        int directorId = director.getId();
        String directorName = director.getName();

        int rowsCount = jdbcTemplate.update(sqlQuery, directorName, directorId);
        if (rowsCount > 0) {
            throw new NotFoundException("Director with ID not found");
        }
        log.info("Director with ID:{} changed.", directorId);

        return get(directorId);
    }

    @Override
    public Director get(int id) {
        String error = String.format("Director with ID:%d not found", id);
        String sql = "SELECT D.id, D.name FROM DIRECTORS D WHERE D.id = ?";

        return jdbcTemplate.query(sql, rowMapper, id)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException(error));
    }

    @Override
    public List<Director> getAll() {
        String sql = "SELECT D.id, D.name FROM DIRECTORS D ORDER BY D.id";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM DIRECTORS WHERE id = ?";
        jdbcTemplate.update(sql, rowMapper, id);

        log.info("Director with ID:{} removed.", id);
    }
}
