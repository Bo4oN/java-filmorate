package ru.yandex.practicum.filmorate.storage.FeedDBStorage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

public interface FeedStorage {
    void addFriendEvent(int userId, int newFriendId);

    void deleteFriendEvent(int userId, int friendId);

    void addLikeEvent(int userId, int entityId);

    void deleteLikeEvent(int userId, int entityId);

    void addReviewEvent(int userId, int reviewId);

    void updateReviewEvent(int userId, int reviewId);

    void deleteReviewEvent(int userId, int reviewId);

    List<Event> getUserFeed(int id);
}
