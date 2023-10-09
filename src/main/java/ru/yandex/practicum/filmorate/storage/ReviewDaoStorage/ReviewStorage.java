package ru.yandex.practicum.filmorate.storage.ReviewDaoStorage;

import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(long id);

    Review getReviewById(long id);

    List<Review> getReviewsByFilmId(long filmId, int count);

    List<Review> getAllReviews(int count);

    void addLike(long reviewId);

    void addDislike(long reviewId);
}
