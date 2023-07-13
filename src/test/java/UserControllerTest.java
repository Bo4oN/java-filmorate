import controllers.UserController;
import model.User;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = UserController.class)
class UserControllerTest {
    private UserController controller = new UserController();

    @Test
    void addUser() {
        User user = new User(1, "qwerty@mail.ru", "login", "Иван",
                    LocalDate.of(2000, 02, 12));

        User user2 = new User(2, "qwerty", "login", "Иван",
                    LocalDate.of(2000, 02, 12));

        User user3 = new User(3, "qwerty@mail.ru", "log in", "Иван",
                LocalDate.of(2000, 02, 12));

        User user4 = new User(3, "qwerty@mail.ru", "login", "Иван",
                LocalDate.of(2040,  10, 12));

        User user5 = new User(1, "qwerty@mail.ru", "login", "",
                LocalDate.of(2000, 02, 12));

        controller.addUser(user);
        assertEquals(user, controller.getUsers().get(0));

        final RuntimeException exception = assertThrows(
                RuntimeException.class, () -> controller.addUser(user2));
        assertEquals("ValidationException: Электронная почта не может быть пустой и должна содержать символ @.",
                exception.getMessage());

        final RuntimeException exception1 = assertThrows(
                RuntimeException.class, () -> controller.addUser(user3));
        assertEquals("ValidationException: Логин не может быть пустым и содержать пробелы.",
                exception1.getMessage());

        final RuntimeException exception2 = assertThrows(
                RuntimeException.class, () -> controller.addUser(user4));
        assertEquals("ValidationException: Дата рождения не может быть в будущем.",
                exception2.getMessage());

        controller.addUser(user5);
        assertEquals(user5.getName(), user5.getLogin(), "Имя не равно логину.");
    }
}