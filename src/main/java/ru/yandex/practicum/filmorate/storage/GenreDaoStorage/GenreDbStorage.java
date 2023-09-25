package ru.yandex.practicum.filmorate.storage.GenreDaoStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage implements GenreStorage {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenre(int id) {
        String sqlQuery = "SELECT * FROM GENRE_TYPES WHERE GENRE_TYPES_ID = :genre_types_id";
        MapSqlParameterSource params = new MapSqlParameterSource("genre_types_id", id);
        try {
            return namedParameterJdbcTemplate.queryForObject(
                sqlQuery,
                    params,
                    (rs, rowNum) -> new Genre(rs.getInt("genre_types_id"), rs.getString("name"))
                );
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанра с ID - " + id + " нет в базе.");
        }
    }

    @Override
    public List<Genre> getAllGenre() {
        String sqlQuery = "SELECT * FROM GENRE_TYPES";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> new Genre(rs.getInt("genre_types_id"), rs.getString("name"))
        );
    }

    @Override
    public void addFilmToGenre(Film film) {
        String sqlQuery = "INSERT INTO GENRES (GENRE_TYPES_ID, FILM_ID) VALUES ( ?, " + film.getId()  + ")";
            jdbcTemplate.batchUpdate(sqlQuery, film.getGenres(), 10,
                    (PreparedStatement ps, Genre genre) -> {
                        ps.setInt(1, genre.getId());
                    }
            );
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
        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> new Genre(rs.getInt("genre_types_id"), rs.getString("name")),
                filmId));
    }
}
