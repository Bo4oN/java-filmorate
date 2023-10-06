package ru.yandex.practicum.filmorate.storage.UserDaoStorage;

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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDaoStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmDaoStorage.FilmDbStorage.FilmMapper;
import ru.yandex.practicum.filmorate.storage.GenreDaoStorage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDaoStorage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Override
    public User add(User data) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", data.getName());
        params.addValue("login", data.getLogin());
        params.addValue("email", data.getEmail());
        params.addValue("birthday", data.getBirthday());

        data.setId(simpleJdbcInsert.executeAndReturnKey(params).intValue());
        return data;
    }

    @Override
    public User update(User data) {
        int id = data.getId();
        String sqlQuery = "UPDATE users SET name = ?, login = ?, email = ?, birthday = ? WHERE user_id = ?;";
        int rowsCount = jdbcTemplate.update(sqlQuery,
                data.getName(),
                data.getLogin(),
                data.getEmail(),
                data.getBirthday(),
                id);
        if (rowsCount > 0) {
            log.info("Пользователь с ID = " + id + " изменен.");
            return data;
        }
        throw new NotFoundException("Пользователь не найден.");
    }

    @Override
    public User delete(int id) {
        String sqlQuery = "DELETE FROM users where user_id=?;";
        return jdbcTemplate.queryForObject(sqlQuery, new UserMapper(), id);
    }

    @Override
    public User get(int id) {
        String sqlQuery = "SELECT * FROM users where user_id=?;";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, new UserMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователя с ID - " + id + " нет в базе.");
        }
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT user_id, name, login, email, birthday FROM users";
        return jdbcTemplate.query(sqlQuery, new UserMapper());
    }

    public class UserMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {

            return new User(rs.getInt("user_id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );
        }
    }

    @Override
    public User addFriend(int firstId, int secondId) {
        try {
            get(secondId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователя с ID - " + secondId + " нет в базе.");
        }
        String sqlQuery = "INSERT INTO friends (USER1_ID, USER2_ID) VALUES ( ?, ? );";
        jdbcTemplate.update(sqlQuery, firstId, secondId);
        log.info("Пользователи с ID - " + firstId + " и ID - " + secondId + " теперь друзья.");
        return get(secondId);
    }

    @Override
    public User deleteFriend(int firstId, int secondId) {
        String sqlQuery = "DELETE FROM friends WHERE user1_id = ? AND user2_id = ?;";
        jdbcTemplate.update(sqlQuery, firstId, secondId);
        log.info("Пользователи с ID - " + firstId + " и ID - " + secondId + " больше не друзья.");
        return get(secondId);
    }


    @Override
    public List<User> getFriendsList(int id) {
        String sqlQuery = "SELECT user_id, name, login, email, birthday" +
                " FROM users WHERE USER_ID" +
                " IN (SELECT USER2_ID FROM friends WHERE USER1_ID = ?);";
        return jdbcTemplate.query(sqlQuery, new UserMapper(), id);
    }

    @Override
    public List<User> getCommonFriends(int firstId, int secondId) {
        String sqlQuery = "SELECT user_id, name, login, email, birthday" +
                " FROM friends" +
                " INNER JOIN PUBLIC.USERS U ON U.USER_ID = friends.USER2_ID " +
                " WHERE friends.USER1_ID = ? AND friends.USER2_ID IN " +
                " (SELECT USER2_ID FROM friends WHERE USER1_ID = ?); ";
        return jdbcTemplate.query(sqlQuery, new UserMapper(), firstId, secondId);
    }

    @Override
    public List<Film> getRecommendations(int id) {
        try {
            get(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователя с ID - " + id + " нет в базе.");
        }
        try {
            String sql = "SELECT l2.user_id " +
                    "FROM likes l1 " +
                    "JOIN likes l2 ON l1.film_id = l2.film_id AND l1.user_id <> l2.user_id " +
                    "WHERE l1.user_id = ? " +
                    "GROUP BY l2.user_id " +
                    "ORDER BY COUNT(DISTINCT l1.film_id) DESC " +
                    "LIMIT 1; "; // возвращает id похожего пользователя

            Integer user2Id = jdbcTemplate.queryForObject(sql, Integer.class, id);

            String sql2 = "SELECT DISTINCT film_id " +
                    "FROM likes " +
                    "WHERE user_id = ? " +
                    "AND film_id NOT IN ( " +
                    "SELECT film_id " +
                    "FROM likes " +
                    "WHERE user_id = ?); "; // возвращает список id фильмов

            List<Integer> recommendedFilmsId = jdbcTemplate.queryForList(sql2, Integer.class, user2Id, id);

            List<Film> recommendedFilms = new ArrayList<>();

            String filmSql = "SELECT FILM_ID, FILMS.NAME AS FN, DESCRIPTION, DURATION, RELEASE_DATE, " +
                    "MPA.MPA_ID, MPA.NAME AS MN  " +
                    "FROM FILMS LEFT JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID WHERE FILM_ID = ? " +
                    "GROUP BY FILMS.FILM_ID, FN, DESCRIPTION, DURATION, RELEASE_DATE, MPA.MPA_ID, MN";

            for (Integer filmId : recommendedFilmsId) {
                Film film = jdbcTemplate.queryForObject(filmSql, new RowMapper<Film>() {
                    @Override
                    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Film film = new Film(rs.getInt("film_id"),
                                rs.getString("fn"),
                                rs.getString("description"),
                                rs.getDate("release_date").toLocalDate(),
                                rs.getLong("duration"),
                                new Mpa(rs.getInt("mpa_id"), rs.getString("mn"))
                        );
                        LinkedHashSet<Genre> set =
                                new LinkedHashSet<>(genreStorage.getGenresOfFilm(rs.getInt("film_id")));
                        film.setGenres(set);
                        return film;
                    }
                }, filmId);
                recommendedFilms.add(film);
            }

            return recommendedFilms;
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }
}