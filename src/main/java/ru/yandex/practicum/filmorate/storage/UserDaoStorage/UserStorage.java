package ru.yandex.practicum.filmorate.storage.UserDaoStorage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User add(User user);

    User update(User user);

    void delete(int id);

    User get(int id);

    List<User> getAll();

    User addFriend(int firstId, int secondId);

    User deleteFriend(int firstId, int secondId);

    List<User> getFriendsList(int id);

    List<User> getCommonFriends(int firstId, int secondId);

    List<Film> getRecommendations(int id);
  
    List<Event> getUserFeed(int id);
}
