package ru.yandex.practicum.filmorate.storage.FilmDaoStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.GenreDaoStorage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", film.getMpa().getId());

        film.setId(simpleJdbcInsert.executeAndReturnKey(params).intValue());

        if (film.getGenres() != null) {
            genreStorage.addFilmToGenre(film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery =
                "UPDATE FILMS SET MPA_ID = ?, NAME = ?, DESCRIPTION = ?, DURATION = ?, RELEASE_DATE = ?" +
                        "WHERE FILM_ID = ?";
        int rowsCount = jdbcTemplate.update(sqlQuery,
                film.getMpa().getId(),
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getId());
        if (rowsCount > 0) {
            log.info("Фильм с ID = " + film.getId() + " изменен.");

            if (film.getGenres() != null) {
                genreStorage.updateFilmToGenre(film);
            }
            return film;
        }
        throw new NotFoundException("Фильм не найден.");
    }

    @Override
    public Film get(int id) {
        String sqlQuery = "SELECT FILM_ID, FILMS.NAME AS FN, DESCRIPTION, DURATION, RELEASE_DATE, " +
                "MPA.MPA_ID, MPA.NAME AS MN  " +
                "FROM FILMS LEFT JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID WHERE FILM_ID = ? " +
                "GROUP BY FILMS.FILM_ID, FN, DESCRIPTION, DURATION, RELEASE_DATE, MPA.MPA_ID, MN";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new FilmMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильма с ID - " + id + " нет в базе.");
        }
    }

    @Override
    public String deleteFilm(int id) {
        String sqlQuery = "DELETE FROM FILMS " +
                "WHERE film_id = " + id;
        int row = jdbcTemplate.update(sqlQuery);
        if (row == 0) {
            throw new NotFoundException("Фильма с ID - " + id + " нет в базе.");
        }
        return "Фильм с id:" + id + " успешно удален.";
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT FILM_ID, FILMS.NAME AS FN, DESCRIPTION, DURATION, RELEASE_DATE, " +
                "MPA.MPA_ID, MPA.NAME AS MN  " +
                "FROM FILMS LEFT JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                "GROUP BY FILMS.FILM_ID, FN, DESCRIPTION, DURATION, RELEASE_DATE, MPA.MPA_ID, MN";
        return jdbcTemplate.query(sqlQuery, new FilmMapper());
    }

    public class FilmMapper implements RowMapper<Film> {

        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film(rs.getInt("film_id"),
                    rs.getString("fn"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getLong("duration"),
                    new Mpa(rs.getInt("mpa_id"), rs.getString("mn"))
            );
            LinkedHashSet<Genre> set = new LinkedHashSet<>(genreStorage.getGenresOfFilm(rs.getInt("film_id")));
            film.setGenres(set);
            return film;
        }
    }

    ;

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES ( ?, ? )";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        int rowCount = jdbcTemplate.update(sqlQuery, filmId, userId);
        if (rowCount < 0) {
            throw new NotFoundException("Лайк от пользователя с ID = " + userId + " не найден");
        }
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILMS.NAME AS FN, DESCRIPTION, DURATION, RELEASE_DATE, " +
                "MPA.MPA_ID, MPA.NAME AS MN, " +
                "COUNT(LIKE_ID) AS film_likes " +
                "FROM FILMS LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID " +
                "LEFT JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                "GROUP BY FILMS.FILM_ID, FN, DESCRIPTION, DURATION, RELEASE_DATE, MPA.MPA_ID, MN " +
                "ORDER BY film_likes DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, new FilmMapper(), count);
    }

    @Override
    public List<Film> getFilmsOfGenre(Genre genre) {
        return null;
    }
}
