package ru.yandex.practicum.filmorate.storage.FeedDBStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedDBStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriendEvent(long userId, long newFriendId) {
        createEvent(userId, newFriendId, EventType.FRIEND, Operation.ADD);
    }

    @Override
    public void deleteFriendEvent(long userId, long friendId) {
        createEvent(userId, friendId, EventType.FRIEND, Operation.REMOVE);
    }

    @Override
    public void addLikeEvent(long userId, long filmId) {
        createEvent(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    @Override
    public void deleteLikeEvent(long userId, long filmId) {
        createEvent(userId, filmId, EventType.LIKE, Operation.REMOVE);
    }

    @Override
    public void addReviewEvent(long userId, long reviewId) {
        createEvent(userId, reviewId, EventType.REVIEW, Operation.ADD);
    }

    @Override
    public void deleteReviewEvent(long userId, long reviewId) {
        createEvent(userId, reviewId, EventType.REVIEW, Operation.REMOVE);
    }

    @Override
    public void updateReviewEvent(long userId, long reviewId) {
        createEvent(userId, reviewId, EventType.REVIEW, Operation.UPDATE);
    }

    @Override
    public List<Event> getUserFeed(long id) {
        String sql = "SELECT COUNT(user_id) FROM users WHERE user_id = " + id;
        if (jdbcTemplate.queryForObject(sql, Integer.class) == 0) {
            throw new NotFoundException("Пользователя с ID - " + id + " нет в базе.");
        }
        sql = "select * from events where user_id = " + id;
        return jdbcTemplate.query(sql, new FeedMapper());
    }

    private void createEvent(long userId, long entityId, EventType eventType, Operation operation) {
        String sql = "SELECT COUNT(user_id) FROM users WHERE user_id = " + userId;
        if (jdbcTemplate.queryForObject(sql, Integer.class) == 0) {
            throw new NotFoundException("Пользователя с ID - " + userId + " нет в базе.");
        }
        sql = "INSERT INTO events(timestamp, user_id, entity_id, event_type, operation) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, System.currentTimeMillis(), userId, entityId,
                String.valueOf(eventType), String.valueOf(operation));
    }

    private static class FeedMapper implements RowMapper<Event> {

        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Event.builder()
                    .timestamp(rs.getLong("timestamp"))
                    .userId(rs.getInt("user_id"))
                    .eventType(EventType.valueOf(rs.getString("event_type")))
                    .operation(Operation.valueOf(rs.getString("operation")))
                    .eventId(rs.getLong("event_id"))
                    .entityId(rs.getLong("entity_id"))
                    .build();
        }
    }
}
