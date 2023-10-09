package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDaoStorage.UserStorage;

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
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        validationUser(user);
        return userStorage.update(user);
    }

    public User getUser(int id) {
        return userStorage.get(id);
    }

    public void deleteUser(int id) {
        userStorage.delete(id);
    }

    public List<User> getUsers() {
        return userStorage.getAll();
    }

    public User addFriend(int firstId, int secondId) {
        return userStorage.addFriend(firstId, secondId);
    }

    public User deleteFriend(int firstId, int secondId) {
        return userStorage.deleteFriend(firstId, secondId);
    }

    public List<User> getFriendsList(int id) {
        return new ArrayList<>(userStorage.getFriendsList(id));
    }

    public List<User> getCommonFriends(int firstId, int secondId) {
        return new ArrayList<>(userStorage.getCommonFriends(firstId, secondId));
    }

    private void validationUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не добавлено, оно становится равно логину - '{}'", user.getLogin());
        }
    }

    public List<Film> getRecommendations(int id) {
        return userStorage.getRecommendations(id);
    }
  
    public List<Event> getUserFeed(int id) {
        return userStorage.getUserFeed(id);
    }
}
