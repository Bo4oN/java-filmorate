package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        validationUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        validationUser(user);
        return userStorage.updateUser(user);
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addFriend(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (user.getFriends().contains(friendId) || friend.getFriends().contains(userId)) {
            log.debug("Пользователь с ID - {}, уже находится в друзьях у пользователя с ID - {}", friendId, userId);
            throw new RuntimeException("Попытка повторного добавления в друзья");
        }
        user.addFriends(friendId);
        friend.addFriends(userId);
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (!user.getFriends().contains(friendId) || !friend.getFriends().contains(userId)) {
            log.debug("Пользователя с ID - {}, нет в друзьях у пользователя с ID - {}", friendId, userId);
        }
        user.deleteFriends(friendId);
        friend.deleteFriends(userId);
        return user;
    }

    public List<User> getFriendsList(int id) {
        User user = userStorage.getUser(id);
        List<User> friendsList = new ArrayList<>();
        for (int i : user.getFriends()) {
            friendsList.add(userStorage.getUser(i));
        }
        return friendsList;
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        List<User> commonFriends = new ArrayList<>();
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        for (Integer i : user.getFriends()) {
            if (friend.getFriends().contains(i)) {
                commonFriends.add(userStorage.getUser(i));
            }
        }
        return commonFriends;
    }

    private void validationUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не добавлено, оно становится равно логину - '{}'", user.getLogin());
        }
    }
}
