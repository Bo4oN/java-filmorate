package ru.yandex.practicum.filmorate.storage.UserDaoStorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User add(User user);

    User update(User user);

    User delete(int id);

    User get(int id);

    List<User> getAll();

    User addFriend(int firstId, int secondId);

    User deleteFriend(int firstId, int secondId);

    List<User> getFriendsList(int id);

    List<User> getCommonFriends(int firstId, int secondId);
}
