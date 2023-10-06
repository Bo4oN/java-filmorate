package ru.yandex.practicum.filmorate.storage.ReviewDaoStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review addReview(Review review) {
        String sqlQuery = "insert into reviews (content, is_positive, user_id, film_id) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.getIsPositive());
            statement.setLong(3, review.getUserId());
            statement.setLong(4, review.getFilmId());
            return statement;
        }, keyHolder);
        String sqlSelect = "SELECT * FROM reviews WHERE review_id = ?";
        return getReviewById(keyHolder.getKey().longValue());
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";

        int rowNum = jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());
        String sqlQuerySelect = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            return getReviewById(review.getReviewId());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Ревью не найдено");
        }
    }

    @Override
    public void deleteReview(long id) {
        String sqlQuery = "DELETE FROM reviews WHERE review_id = ?";
        int rowsNum = jdbcTemplate.update(sqlQuery, id);
        if (rowsNum == 0) {
            throw new NotFoundException("Отзыв не найден");
        }
    }

    @Override
    public Review getReviewById(long id) {
        String sqlQuery = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Ревью не найдено");
        }
    }

    @Override
    public List<Review> getReviewsByFilmId(long filmId, int count) {
        String sqlQuery = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Ревью не найдено");
        }
    }

    @Override
    public List<Review> getAllReviews(int count) {
        String sqlQuery = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        try {
            return jdbcTemplate.query(sqlQuery, new DataClassRowMapper<>(Review.class), count);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Ревью не найдено");
        }
    }

    @Override
    public void addLike(long reviewId) {
        String sqlQuery = "UPDATE reviews SET useful = useful+1 WHERE review_id = ?";
        int rowsNum = jdbcTemplate.update(sqlQuery, reviewId);
        if (rowsNum == 0) {
            throw new NotFoundException("Ревью не найдено");
        }
    }

    @Override
    public void addDislike(long reviewId) {
        String sqlQuery = "UPDATE reviews SET useful = useful-1 WHERE review_id = ?";
        int rowsNum = jdbcTemplate.update(sqlQuery, reviewId);
        if (rowsNum == 0) {
            throw new NotFoundException("Ревью не найдено");
        }
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review(
                resultSet.getLong("review_id"),
                resultSet.getString("content"),
                resultSet.getBoolean("is_positive"),
                resultSet.getInt("user_id"),
                resultSet.getInt("film_id"),
                resultSet.getInt("useful")
        );
        return review;

    }
}
