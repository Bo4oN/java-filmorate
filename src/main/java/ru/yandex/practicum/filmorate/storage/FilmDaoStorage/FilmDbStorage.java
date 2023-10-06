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
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.DirectorDaoStorage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.GenreDaoStorage.GenreStorage;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final RowMapper<Film> rowMapper;

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

        return updateFilmData(film);
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

            return updateFilmData(film);
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
            return jdbcTemplate.queryForObject(sqlQuery, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильма с ID - " + id + " нет в базе.");
        }
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT FILM_ID, FILMS.NAME AS FN, DESCRIPTION, DURATION, RELEASE_DATE, " +
                "MPA.MPA_ID, MPA.NAME AS MN  " +
                "FROM FILMS LEFT JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                "GROUP BY FILMS.FILM_ID, FN, DESCRIPTION, DURATION, RELEASE_DATE, MPA.MPA_ID, MN";
        return jdbcTemplate.query(sqlQuery, rowMapper);
    }

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
        return jdbcTemplate.query(sqlQuery, rowMapper, count);
    }

    @Override
    public List<Film> getFilmsOfGenre(Genre genre) {
        return null;
    }

    /**
     * Возвращает список фильмов режиссера
     * отсортированных по количеству лайков или году выпуска.
     *
     * @param filmSortBy filmSortBy=[year,likes]
     * @return список фильмов режиссера
     */
    @Override
    public List<Film> getFilmDirector(int directorId, FilmSortBy filmSortBy) {
        Map<String, String> params = filmSortBy.getParams();
        String sqlQuery = "SELECT F.film_id,"
                + " F.name FN,"
                + " F.description,"
                + " F.duration,"
                + " F.release_date,"
                + " MPA.mpa_id,"
                + " MPA.name MN,"
                + params.get("SELECT")
                + "FROM FILMS_DIRECTOR FD "
                + "LEFT JOIN FILMS F ON FD.film_id = F.film_id "
                + params.get("LEFT JOIN")
                + "LEFT JOIN MPA ON F.MPA_ID = MPA.MPA_ID "
                + " WHERE FD.director_id = ? "
//                + "GROUP BY F.FILM_ID "
                + params.get("ORDER BY") + ";";
        return jdbcTemplate.query(sqlQuery, rowMapper, directorId);
    }

    private Film updateFilmData(Film film) {
        if (film.getGenres() != null) {
            genreStorage.updateFilmToGenre(film);
        }
        if (film.getDirectors() != null) {
            directorStorage.updateFilmDirector(film);
        }
        return get(film.getId());
    }
}
