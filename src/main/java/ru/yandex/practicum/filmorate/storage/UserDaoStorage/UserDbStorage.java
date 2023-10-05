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
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

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
    public String delete(int id) {
        String sqlQuery = "DELETE FROM users where user_id = " + id;
        int row = jdbcTemplate.update(sqlQuery);
        if (row == 0) {
            throw new NotFoundException("Пользователь не найден.");
        }
        return "Пользователь с id:" + id + " успешно удален.";
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
}