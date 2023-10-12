package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;

    @ResponseBody
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на добавление пользователя.");
        return userService.addUser(user);
    }

    @ResponseBody
    @PutMapping
    public User updateUsers(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя.");
        return userService.updateUser(user);
    }

    @ResponseBody
    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        log.info("Получен запрос на получение пользователя с ID - {}.", id);
        return userService.getUser(Integer.parseInt(id));
    }

    @ResponseBody
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable String id) {
        log.info("Получен запрос на удаление пользователя с ID - {}.", id);
        userService.deleteUser(Integer.parseInt(id));
    }

    @ResponseBody
    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @ResponseBody
    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable String id, @PathVariable String friendId) {
        return userService.addFriend(Integer.parseInt(id), Integer.parseInt(friendId));
    }

    @ResponseBody
    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable String id, @PathVariable String friendId) {
        return userService.deleteFriend(Integer.parseInt(id), Integer.parseInt(friendId));
    }

    @ResponseBody
    @GetMapping("/{id}/friends")
    public List<User> getFriendsList(@PathVariable String id) {
        return userService.getFriendsList(Integer.parseInt(id));
    }

    @ResponseBody
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable String id, @PathVariable String otherId) {
        return userService.getCommonFriends(Integer.parseInt(id), Integer.parseInt(otherId));
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable int id) {
        return userService.getRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getUserFeed(@PathVariable String id) {
        return userService.getUserFeed(Integer.parseInt(id));
    }
}