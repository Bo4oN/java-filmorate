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
import ru.yandex.practicum.filmorate.model.FilmSortBy;
import ru.yandex.practicum.filmorate.storage.DirectorDaoStorage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FeedDBStorage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.GenreDaoStorage.GenreStorage;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmRowMapper;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final FeedStorage feedStorage;

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
        int filmId = film.getId();
        String sqlQuery =
                "UPDATE FILMS SET MPA_ID = ?, NAME = ?, DESCRIPTION = ?, DURATION = ?, RELEASE_DATE = ?" +
                        "WHERE FILM_ID = ?";
        int rowsCount = jdbcTemplate.update(sqlQuery,
                film.getMpa().getId(),
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                filmId);
        if (rowsCount > 0) {
            log.info("Фильм с ID = {} изменен.", filmId);

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
            return jdbcTemplate.queryForObject(sqlQuery, filmRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильма с ID - " + id + " нет в базе.");
        }
    }

    @Override
    public void deleteFilm(int id) {
        String sqlQuery = "DELETE FROM FILMS " +
                "WHERE film_id = " + id;
        int row = jdbcTemplate.update(sqlQuery);
        if (row == 0) {
            throw new NotFoundException("Фильма с ID - " + id + " нет в базе.");
        }
        log.info("Фильм с id:" + id + " успешно удален.");
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT FILM_ID, FILMS.NAME AS FN, DESCRIPTION, DURATION, RELEASE_DATE, " +
                "MPA.MPA_ID, MPA.NAME AS MN  " +
                "FROM FILMS LEFT JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                "GROUP BY FILMS.FILM_ID, FN, DESCRIPTION, DURATION, RELEASE_DATE, MPA.MPA_ID, MN";
        return jdbcTemplate.query(sqlQuery, filmRowMapper);
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "SELECT COUNT(user_id) FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        Integer i = jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
        if (i == null || i == 0) {
            String sqlQuery = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES ( ?, ? )";
            jdbcTemplate.update(sqlQuery, filmId, userId);
        }
        feedStorage.addLikeEvent(userId, filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        int rowCount = jdbcTemplate.update(sqlQuery, filmId, userId);
        if (rowCount < 0) {
            throw new NotFoundException("Лайк от пользователя с ID = " + userId + " не найден");
        }
        feedStorage.deleteLikeEvent(userId, filmId);
    }

    @Override
    public List<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        String sqlQuery = "SELECT F.film_id,"
                + " F.name AS FN,"
                + " F.description,"
                + " F.duration,"
                + " F.release_date, "
                + " MPA.mpa_id, "
                + " MPA.name AS MN, "
                + " COUNT(like_id) FILM_LIKES, "
                + "FROM FILMS F "
                + "LEFT JOIN LIKES L ON F.film_id = L.film_id "
                + "LEFT JOIN MPA ON F.mpa_id = MPA.mpa_id "
                + getSelectionString(genreId, year)
                + " GROUP BY F.film_id "
                + " ORDER BY FILM_LIKES DESC "
                + " LIMIT ?";

        return getTopFilmsSelection(sqlQuery, count, genreId, year);
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String sqlQuery;
        if (by.contains("title") && by.contains("director")) {
            sqlQuery = "SELECT FILMS.FILM_ID, FILMS.NAME AS FN, DESCRIPTION, DURATION, RELEASE_DATE," +
                    "MPA.MPA_ID, MPA.NAME AS MN, COUNT(LIKE_ID) as films_like " +
                    "FROM FILMS " +
                    "LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID " +
                    "LEFT JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                    "WHERE LOWER(FILMS.NAME) LIKE LOWER(CONCAT('%',?,'%')) " +
                    "GROUP BY FILMS.FILM_ID, FILMS.NAME, DESCRIPTION, DURATION, RELEASE_DATE, " +
                    "MPA.MPA_ID, MPA.NAME " +
                    "UNION " +
                    "(SELECT FILMS.FILM_ID, FILMS.NAME AS FN, DESCRIPTION, DURATION, RELEASE_DATE, " +
                    "MPA.MPA_ID, MPA.NAME AS MN, COUNT(LIKE_ID) as films_like " +
                    "FROM FILMS " +
                    "LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID " +
                    "LEFT JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                    "WHERE FILMS.FILM_ID IN (SELECT FILMS_DIRECTOR.film_id " +
                    "FROM FILMS_DIRECTOR " +
                    "LEFT JOIN DIRECTORS ON DIRECTOR_ID = ID " +
                    "WHERE LOWER(DIRECTORS.NAME) LIKE LOWER(CONCAT('%',?,'%'))) " +
                    "GROUP BY FILMS.FILM_ID, FILMS.NAME, DESCRIPTION, DURATION, RELEASE_DATE, " +
                    "MPA.MPA_ID, MPA.NAME) " +
                    "ORDER BY films_like DESC";

            return  jdbcTemplate.query(sqlQuery, filmRowMapper, query, query);

        } else if (by.contains("title")) {
            sqlQuery = "SELECT FILMS.FILM_ID, FILMS.NAME AS FN, DESCRIPTION, DURATION, RELEASE_DATE," +
                    "MPA.MPA_ID, MPA.NAME AS MN, COUNT(LIKE_ID) as films_like " +
                    "FROM FILMS " +
                    "LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID " +
                    "LEFT JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                    "WHERE LOWER(FILMS.NAME) LIKE LOWER(CONCAT('%',?,'%')) " +
                    "GROUP BY FILMS.FILM_ID, FILMS.NAME, DESCRIPTION, DURATION, RELEASE_DATE, " +
                    "MPA.MPA_ID, MPA.NAME " +
                    "ORDER BY films_like DESC";

            return  jdbcTemplate.query(sqlQuery, filmRowMapper, query);

        } else if (by.contains("director")) {
            sqlQuery = "SELECT FILMS.FILM_ID, FILMS.NAME AS FN, DESCRIPTION, DURATION, RELEASE_DATE," +
                    "MPA.MPA_ID, MPA.NAME AS MN, COUNT(LIKE_ID) as films_like " +
                    "FROM FILMS " +
                    "LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.FILM_ID " +
                    "LEFT JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                    "WHERE FILMS.FILM_ID IN (SELECT FILMS_DIRECTOR.film_id " +
                    "FROM FILMS_DIRECTOR " +
                    "LEFT JOIN DIRECTORS ON DIRECTOR_ID = ID " +
                    "WHERE LOWER(DIRECTORS.NAME) LIKE LOWER(CONCAT('%',?,'%'))) " +
                    "GROUP BY FILMS.FILM_ID, FILMS.NAME, DESCRIPTION, DURATION, RELEASE_DATE, " +
                    "MPA.MPA_ID, MPA.NAME " +
                    "ORDER BY films_like DESC";

            return  jdbcTemplate.query(sqlQuery, filmRowMapper, query);

        } else {
            throw new NotFoundException("Параметр поиска задан не корректно - " + by);
        }
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
        String sqlQuery = "SELECT F.film_id, "
                + "F.name AS FN, "
                + "F.description, "
                + "F.duration, "
                + "F.release_date, "
                + "MPA.mpa_id, "
                + "MPA.name as MN, "
                + params.get("SELECT")
                + "FROM FILMS_DIRECTOR AS FD "
                + "LEFT JOIN FILMS AS F ON FD.film_id = F.film_id "
                + params.get("LEFT JOIN")
                + "LEFT JOIN MPA ON F.MPA_ID = MPA.MPA_ID "
                + "WHERE FD.director_id = ? "
                + "GROUP BY F.FILM_ID "
                + params.get("ORDER BY") + ";";
        return jdbcTemplate.query(sqlQuery, filmRowMapper, directorId);
    }

    private String getSelectionString(Integer genreId, Integer year) {
        String search = "";

        if (genreId == null) {
            if (year != null) {
                search = " WHERE EXTRACT(YEAR FROM CAST (F.release_date as DATE)) = ? ";
            }
        } else {
            search = " LEFT JOIN GENRES G ON F.film_id = G.film_id WHERE G.genre_types_id = ? ";
            if (year != null) {
                search += " AND EXTRACT(YEAR FROM CAST (F.release_date as DATE)) = ? ";
            }
        }
        return search;
    }

    private List<Film> getTopFilmsSelection(String sqlQuery, Integer count, Integer genreId, Integer year) {
        if (genreId == null) {
            if (year != null) {
                return jdbcTemplate.query(sqlQuery, filmRowMapper, year, count);
            }
            return jdbcTemplate.query(sqlQuery, filmRowMapper, count);
        } else {
            if (year != null) {
                return jdbcTemplate.query(sqlQuery, filmRowMapper, genreId, year, count);
            }
            return jdbcTemplate.query(sqlQuery, filmRowMapper, genreId, count);
        }
    }

    @Override
    public List<Film> getCommonTopFilm(int userId, int friendId) {
        String sql = "SELECT " +
                "f.FILM_ID, " +
                "f.NAME AS FN, " +
                "f.DESCRIPTION, " +
                "f.DURATION, " +
                "f.RELEASE_DATE, " +
                "m.MPA_ID, " +
                "m.NAME AS MN, " +
                "COUNT(l.LIKE_ID) AS film_likes " +
                "FROM " +
                "FILMS f " +
                "LEFT JOIN LIKES l ON f.FILM_ID = l.FILM_ID " +
                "LEFT JOIN MPA m ON f.MPA_ID = m.MPA_ID " +
                "WHERE " +
                "l.USER_ID = ? " +
                "AND f.FILM_ID IN (SELECT film_id FROM likes WHERE user_id = ?) " +
                "GROUP BY " +
                "f.FILM_ID, FN, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, m.MPA_ID, MN " +
                "ORDER BY " +
                "film_likes DESC; ";

        return jdbcTemplate.query(sql, filmRowMapper, userId, friendId);
    }

    private Film updateFilmData(Film film) {
        if (film.getGenres() != null) {
            genreStorage.updateFilmToGenre(film);
        } else {
            genreStorage.deleteFilmToGenre(film);
        }
        if (film.getDirectors() != null) {
            directorStorage.updateFilmDirector(film);
        } else {
            directorStorage.deleteFilmDirector(film);
        }
        return get(film.getId());
    }
}
