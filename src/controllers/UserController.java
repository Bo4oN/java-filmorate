import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        log.info("Получен запрос на добовление пользователя.");
        try {
            validationUser(user);
            log.info("Пользователь успешно добавлен.");
        } catch (ValidationException e) {
            log.debug("Пользователь не прошел валидацию.");
            throw new RuntimeException(e);
        }
        return user;
    }
    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        log.info("Получен запрос на обновление пользователя.");
        if (users.containsKey(user.getId())) {
            try {
                validationUser(user);
                log.info("Пользователь успешно изменен.");
            } catch (ValidationException e) {
                log.debug("Пользователь не прошел валидацию.");
                throw new RuntimeException(e);
            }
        } else {
            log.debug("Обновляется не добавленный пользователь, его ID - ", user.getId());
            throw new RuntimeException("Не добавлен пользователь с ID - " + user.getId());

        }
        return user;
    }
    @GetMapping("/users")
    public List<User> getUsers() {
        List<User> list = new ArrayList<>(users.values());
        return list;
    }
    private void validationUser(@Valid User user) {
        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            log.debug("У пользователя некорректный Email.");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");

        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.debug("Недопустимые символы в логине пользователя.");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");

        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Невалидная дата рождения пользователя");
            throw new ValidationException("Дата рождения не может быть в будущем.");

        } else if (user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не добавлено, оно становится равно логину - ", user.getLogin());
        } else {
            users.put(user.getId(), user);
        }
    }
}