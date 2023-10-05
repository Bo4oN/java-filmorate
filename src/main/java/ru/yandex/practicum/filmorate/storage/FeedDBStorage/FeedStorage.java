package ru.yandex.practicum.filmorate.storage.FeedDBStorage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

public interface FeedStorage {
    void addFriendEvent(int userId, int newFriendId);

    void deleteFriend(int userId, int friendId);

    void addLike(int userId, int filmId);

    void deleteLike(int userId, int filmId);

    void addReview(int userId, int filmId);

    void updateReview(int userId, int filmId);

    void deleteReview(int userId, int filmId);

    List<Event> getUserFeed(int id);
}
