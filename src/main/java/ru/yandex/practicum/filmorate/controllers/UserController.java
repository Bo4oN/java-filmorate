package ru.yandex.practicum.filmorate.controllers;

import java.time.LocalDate;
import java.util.List;

import exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequestMapping("/users")
@RestController
public class UserController extends SimpleController<User> {

    @Override
    @ResponseBody
    @PostMapping
    public User addEntity(@Valid @RequestBody User user) {
        log.info("Получен запрос на добовление пользователя.");
        validationUser(user);
        log.info("Пользователь успешно добавлен.");
        return super.addEntity(user);
    }

    @Override
    @ResponseBody
    @PutMapping
    public User updateEntity(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя.");
        validationUser(user);
        log.info("Пользователь успешно обновлен.");
        return super.updateEntity(user);
    }

    @ResponseBody
    @GetMapping
    public List<User> getUsers() {
        return super.getEntity();
    }

    private void validationUser(User user) {
        if (!user.getEmail().contains("@") || user.getEmail().isBlank() || user.getEmail() == null) {
            log.debug("У пользователя некорректный Email.");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Недопустимые символы в логине пользователя.");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Невалидная дата рождения пользователя");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Мы тут {} {}", user.getName(), user.getLogin());
            user.setName(user.getLogin());
            log.info("Имя пользователя не добавлено, оно становится равно логину - '{}'", user.getLogin());
        }
    }
}