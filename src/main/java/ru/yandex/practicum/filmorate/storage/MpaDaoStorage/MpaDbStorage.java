package ru.yandex.practicum.filmorate.storage.MpaDaoStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final RowMapper<Mpa> rowMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getMpa(int id) {
        String sqlQuery = "SELECT * FROM MPA WHERE MPA_ID = ?";
        return jdbcTemplate.query(sqlQuery, rowMapper, id)
                .stream()
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundException("Рейтинга с ID - " + id + " нет в базе.")
                );
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(sqlQuery, rowMapper);
    }
}
