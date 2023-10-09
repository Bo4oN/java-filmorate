package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmDaoStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewDaoStorage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserDaoStorage.UserStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage, UserStorage userStorage, FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review createReview(Review review) {
        userStorage.get(review.getUserId());
        filmStorage.get(review.getFilmId());
        return reviewStorage.addReview(review);
    }

    public void deleteReview(long id) {
        reviewStorage.deleteReview(id);
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

    public void addLike(long reviewId) {
        reviewStorage.addLike(reviewId);
    }

    public void addDislike(long reviewId) {
        reviewStorage.addDislike(reviewId);
    }
}
