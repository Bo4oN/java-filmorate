package ru.yandex.practicum.filmorate.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;

    @ResponseBody
    @PostMapping
    public Entity addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на добавление пользователя.");
        return userService.addUser(user);
    }

    @ResponseBody
    @PutMapping
    public Entity updateUsers(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя.");
        return userService.updateUser(user);
    }

    @ResponseBody
    @GetMapping("/{id}")
    public Entity getUserById(@PathVariable String id) {
        log.info("Получен запрос на получение пользователя с ID - {}.", id);
        return userService.getUser(Integer.parseInt(id));
    }

    @ResponseBody
    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @ResponseBody
    @PutMapping("/{id}/friends/{friendId}")
    public Entity addFriend(@PathVariable String id, @PathVariable String friendId) {
        return userService.addFriend(Integer.parseInt(id), Integer.parseInt(friendId));
    }

    @ResponseBody
    @DeleteMapping("/{id}/friends/{friendId}")
    public Entity deleteFriend(@PathVariable String id, @PathVariable String friendId) {
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

    @GetMapping("/{id}/feed")
    public List<Event> getUserFeed(@PathVariable String id) {
        return userService.getUserFeed(Integer.parseInt(id));
    }
}