package ru.yandex.practicum.filmorate.controllers;

import exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequestMapping("/films")
@RestController
public class FilmController extends SimpleController <Film> {

    private static final LocalDate BIRTHDAY_MOVIE = LocalDate.of(1895, 12, 28);

    @Override
    @ResponseBody
    @PostMapping
    public Film addEntity(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добовление фильма.");
        validationFilm(film);
        log.info("Добавлен фильм - " + film);
        return super.addEntity(film);
    }

    @Override
    @ResponseBody
    @PutMapping
    public Film updateEntity(@Valid @RequestBody Film film) {
        log.info("Получен запрос на изменение фильма.");
        validationFilm(film);
        log.info("Фильм успешно изменен на - '{}'", film);
        return super.updateEntity(film);
    }

    @ResponseBody
    @GetMapping
    public List<Film> getFilms() {
        return super.getEntity();
    }

    private void validationFilm(Film film) {
        /*if (film.getName().isBlank()) {
            log.debug("Фильм не имеет названия.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }*/

        if (film.getDescription().length() > 200) {
            log.debug("В описании более 200 символов.");
            throw new ValidationException("В описании фильма должно быть не более 200 символов.");
        }

        if (film.getReleaseDate().isBefore(BIRTHDAY_MOVIE)) {
            log.debug("Не валидная дата премьеры.");
            throw new ValidationException("Дата премьеры фильма не может быть раньше 28 декабря 1985г.");
        }

        if (film.getDuration() <= 0) {
            log.debug("Отрицательное значение в продолжительности фильма.");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной.");
        }
    }
}