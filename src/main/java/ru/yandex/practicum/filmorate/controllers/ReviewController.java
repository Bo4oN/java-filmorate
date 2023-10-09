package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @ResponseBody
    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("Запрос на добавление отзыва" + review.getIsPositive());
        return reviewService.createReview(review);
    }

    @ResponseBody
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Запрос на обновление отзыва");
        return reviewService.updateReview(review);
    }

    @ResponseBody
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        log.info("Запрос на удаление отзыва");
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable long id) {
        log.info("Запрос на получение отзыва по id");
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public Collection<Review> getReviewByFilmId(@RequestParam(defaultValue = "-1") int filmId, @RequestParam(defaultValue = "10") int count) {
        log.info("Запрос на получение отзывов по id фильма");
        return reviewService.getReviews(filmId, count);
    }

    @ResponseBody
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable int userId) {
        log.info("Лайк!");
        reviewService.addLike(id);
    }

    @ResponseBody
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable int userId) {
        log.info("Дизлайк!");
        reviewService.addDislike(id);
    }

    @ResponseBody
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable int userId) {
        log.info("Удалить лайк!");
        reviewService.addDislike(id);
    }

    @ResponseBody
    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable long id, @PathVariable int userId) {
        log.info("Удалить дизлайк!");
        reviewService.addLike(id);
    }
}
