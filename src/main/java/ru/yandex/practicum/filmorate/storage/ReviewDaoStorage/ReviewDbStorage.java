package ru.yandex.practicum.filmorate.storage.ReviewDaoStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedDBStorage.FeedStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FeedStorage feedStorage;

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
        long reviewId = keyHolder.getKey().longValue();
        feedStorage.addReviewEvent(review.getUserId(), reviewId);
        return getReviewById(reviewId);
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());
        try {
            final Review rev = getReviewById(review.getReviewId());
            feedStorage.updateReviewEvent(rev.getUserId(), rev.getReviewId());
            return rev;
        } catch (EmptyResultDataAccessException e) {
            String error = String.format("Review with ID:%d not found", review.getReviewId());
            log.error(error);
            throw new NotFoundException(error);
        }
    }

    @Override
    public void deleteReview(long id) {
        int userId = jdbcTemplate.queryForObject("SELECT user_id FROM reviews WHERE review_id = ?", Integer.class, id);
        String sqlQuery = "DELETE FROM reviews WHERE review_id = ?";
        int rowsNum = jdbcTemplate.update(sqlQuery, id);
        if (rowsNum == 0) {
            String error = String.format("Review with ID:%d not found", id);
            log.error(error);
            throw new NotFoundException(error);
        }
        feedStorage.deleteReviewEvent(userId, id);
    }

    @Override
    public Review getReviewById(long id) {
        String sqlQuery = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            String error = String.format("Review with ID:%d not found", id);
            log.error(error);
            throw new NotFoundException(error);
        }
    }

    @Override
    public List<Review> getReviewsByFilmId(long filmId, int count) {
        String sqlQuery = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
        } catch (EmptyResultDataAccessException e) {
            String error = String.format("Review for film with ID:%d not found", filmId);
            log.error(error);
            throw new NotFoundException(error);
        }
    }

    @Override
    public List<Review> getAllReviews(int count) {
        String sqlQuery = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Ревью не найдено");
        }
    }

    @Override
    public void addLike(long reviewId) {
        String sqlQuery = "UPDATE reviews SET useful = useful+1 WHERE review_id = ?";
        int rowsNum = jdbcTemplate.update(sqlQuery, reviewId);
        if (rowsNum == 0) {
            String error = String.format("Review with ID:%d not found", reviewId);
            log.error(error);
            throw new NotFoundException(error);
        }
    }

    @Override
    public void addDislike(long reviewId) {
        String sqlQuery = "UPDATE reviews SET useful = useful-1 WHERE review_id = ?";
        int rowsNum = jdbcTemplate.update(sqlQuery, reviewId);
        if (rowsNum == 0) {
            String error = String.format("Review with ID:%d not found", reviewId);
            log.error(error);
            throw new NotFoundException(error);
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
