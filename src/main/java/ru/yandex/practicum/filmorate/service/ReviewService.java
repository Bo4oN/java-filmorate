package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedDBStorage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmDaoStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewDaoStorage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserDaoStorage.UserStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage, UserStorage userStorage,
                         FilmStorage filmStorage, FeedStorage feedStorage, JdbcTemplate jdbcTemplate) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.feedStorage = feedStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Review createReview(Review review) {
        userStorage.get(review.getUserId());
        filmStorage.get(review.getFilmId());
        //feedStorage.addReviewEvent(review.getUserId(), review.getFilmId());
        return reviewStorage.addReview(review);
    }

    public void deleteReview(/*long*/int id) {
        int userId = jdbcTemplate.queryForObject("SELECT user_id FROM reviews WHERE review_id = ?", Integer.class, id);
        int filmId = jdbcTemplate.queryForObject("SELECT film_id FROM reviews WHERE review_id = ?", Integer.class, id);
        reviewStorage.deleteReview(id);
        feedStorage.deleteReviewEvent(userId, filmId);
    }

    public Review getReviewById(long id) {
        return reviewStorage.getReviewById(id);
    }

    public Review updateReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    public List<Review> getReviews(long filmId, int count) {
        return (filmId == -1 ? reviewStorage.getAllReviews(count) : reviewStorage.getReviewsByFilmId(filmId, count));
    }

    public void addLike(/*long*/ int reviewId, int userId) {
        reviewStorage.addLike(reviewId, userId);
        //feedStorage.addLikeEvent(userId, reviewId);
    }

    public void addDislike(/*long*/ int reviewId, int userId) {
        reviewStorage.addDislike(reviewId, userId);
        //feedStorage.addLikeEvent(userId, reviewId);
    }

    public void deleteLike(int reviewId, int userId) {
        reviewStorage.addDislike(reviewId, userId);
        //feedStorage.deleteLikeEvent(userId, reviewId);
    }

    public void deleteDislike(int reviewId, int userId) {
        reviewStorage.addLike(reviewId, userId);
        //feedStorage.deleteLikeEvent(userId, reviewId);
    }
}
