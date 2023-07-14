package controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @ResponseBody
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на добовление пользователя.");
        validationUser(user);
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен.");
        return user;
    }

    @ResponseBody
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя.");
        if (users.containsKey(user.getId())) {
                validationUser(user);
                users.put(user.getId(), user);
                log.info("Пользователь успешно изменен.");
        } else {
            log.debug("Обновляется не добавленный пользователь, его ID - '{}'", user.getId());
            throw new ValidationException("Не добавлен пользователь с ID - " + user.getId());

        }
        return user;
    }

    @ResponseBody
    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
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

        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не добавлено, оно становится равно логину - '{}'", user.getLogin());
        }
    }
}