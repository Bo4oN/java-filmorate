package ru.yandex.practicum.filmorate.storage.FeedDBStorage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedStorage {
    void addFriendEvent(long userId, long newFriendId);

    void deleteFriendEvent(long userId, long friendId);

    void addLikeEvent(long userId, long entityId);

    void deleteLikeEvent(long userId, long entityId);

    void addReviewEvent(long userId, long reviewId);

    void updateReviewEvent(long userId, long reviewId);

    void deleteReviewEvent(long userId, long reviewId);

    List<Event> getUserFeed(long id);
}
