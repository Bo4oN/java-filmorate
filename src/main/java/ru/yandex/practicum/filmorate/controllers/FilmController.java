package ru.yandex.practicum.filmorate.controllers;

import exceptions.ValidationException;
import model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int nextId = 1;
    private static final LocalDate BIRTHDAY_MOVIE = LocalDate.of(1895, 12, 28);

    @ResponseBody
    @PostMapping(value = "/films")
    public Film addFilm(@RequestBody Film film) {
        log.info("Получен запрос на добовление фильма.");
        validationFilm(film);
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм - " + film);
        return film;
    }

    @ResponseBody
    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        log.info("Получен запрос на изменение фильма.");
        if (films.containsKey(film.getId())) {
            validationFilm(film);
            films.put(film.getId(), film);
            log.info("Фильм успешно изменен на - '{}'", film);
        } else {
            log.debug("Обновляется несуществующий фильм.");
            throw new ValidationException("Фильм с таким ID не был добавлен.");
        }
        return film;
    }

    @ResponseBody
    @GetMapping("/films")
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private void validationFilm(Film film) {
        if (film.getName().isBlank()) {
            log.debug("Фильм не имеет названия.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }

        if (film.getDescription().length() > 200) {
            log.debug("В описании более 200 символов.");
            throw new ValidationException("В описании фильма должно быть не более 200 символов.");
        }

        if (film.getReleaseDate().isBefore(BIRTHDAY_MOVIE)) {
            log.debug("Не валидная дата премьеры.");
            throw new ValidationException("Дата премьеры фильма не может быть раньше 28 декабря 1985г.");
        }

        if (film.getDuration().isNegative()) {
            log.debug("Отрицательное значение в продолжительности фильма.");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной.");
        }
    }
}