package ru.yandex.practicum.filmorate.storage.MpaDaoStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getMpa(int id) {
        String sqlQuery = "SELECT * FROM MPA WHERE MPA_ID = ?";
        try {
        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("name")),
                id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинга с ID - " + id + " нет в базе.");
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(sqlQuery,
                ((rs, rowNum) -> new Mpa(rs.getInt("mpa_id"), rs.getString("name")))
        );
    }
}
