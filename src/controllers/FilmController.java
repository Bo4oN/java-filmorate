import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class FilmController {
    private HashMap<Integer, Film> films = new HashMap<>();
    private static final LocalDate BIRTHDAY_MOVIE = LocalDate.of(1895, 12, 28);

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        log.info("Получен запрос на добовление фильма.");
        try {
            validationFilm(film);
            log.info("Добавлен фильм - " + film.toString());
        } catch (ValidationException e) {
            log.debug("Фильм не прошел валидацию.", e);
            throw new RuntimeException(e);
        }
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        log.info("Получен запрос на изменение фильма.");
        if (films.containsKey(film.getId())) {
            try {
                validationFilm(film);
                log.info("Добавлен фильм - ", film);
            } catch (ValidationException e) {
                log.debug("Фильм не прошел валидацию.", e);
                throw new RuntimeException(e);
            }
            log.info("Фильм успешно изменен на - ", film);
        } else {
            log.debug("Обновляется несуществующий фильм.");
            throw new RuntimeException("Фильм с таким ID не был добавлен.");
        }
        return film;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        List<Film> list = new ArrayList<>(films.values());
        return list;
    }

    private void validationFilm(Film film) {
        if (film.getName().isBlank()) {
            log.debug("Фильм не имеет названия.");
            throw new ValidationException("Название фильма не может быть пустым.");

        } else if (film.getDescription().length() > 200) {
            log.debug("В описании более 200 символов.");
            throw new ValidationException("В описании фильма должно быть не более 200 символов.");

        } else if (film.getReleaseDate().isBefore(BIRTHDAY_MOVIE)) {
            log.debug("Не валидная дата премьеры.");
            throw new ValidationException("Дата премьеры фильма не может быть раньше 28 декабря 1985г.");

        } else if (film.getDuration().isNegative()) {
            log.debug("Отрицательное значение в продолжительности фильма.");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной.");

        } else {
            films.put(film.getId(), film);
        }
    }
}