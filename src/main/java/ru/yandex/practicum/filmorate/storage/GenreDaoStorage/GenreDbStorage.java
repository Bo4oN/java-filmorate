package ru.yandex.practicum.filmorate.storage.GenreDaoStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> rowMapper;

    @Override
    public Genre getGenre(int id) {
        String sqlQuery = "SELECT * FROM GENRE_TYPES WHERE GENRE_TYPES_ID = ?";
        return jdbcTemplate.query(sqlQuery, rowMapper, id)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Жанра с ID - " + id + " нет в базе."));
    }

    @Override
    public List<Genre> getAllGenre() {
        String sqlQuery = "SELECT * FROM GENRE_TYPES";
        return jdbcTemplate.query(sqlQuery, rowMapper);
    }

    @Override
    public void addFilmToGenre(Film film) {
        String sqlQuery = "INSERT INTO GENRES (GENRE_TYPES_ID, FILM_ID) VALUES ( ?, ?)";
        List<Genre> genreList = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, genreList.get(i).getId());
                ps.setInt(2, film.getId());
            }

            @Override
            public int getBatchSize() {
                return genreList.size();
            }
        });
    }

    @Override
    public void updateFilmToGenre(Film film) {
        String sqlQuery = "DELETE FROM genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        addFilmToGenre(film);
    }

    @Override
    public Set<Genre> getGenresOfFilm(int filmId) {
        String sqlQuery = "SELECT * FROM GENRE_TYPES WHERE GENRE_TYPES_ID IN" +
                " (SELECT DISTINCT GENRE_TYPES_ID FROM GENRES WHERE FILM_ID = ?)";
        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, rowMapper, filmId));
    }
}
