package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
@RestController
public class FilmController {

    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @ResponseBody
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма.");
        return filmService.addFilm(film);
    }

    @ResponseBody
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на изменение фильма.");
        return filmService.updateFilm(film);
    }

    @ResponseBody
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable String id) {
        log.info("Получен запрос на получения фильма с id - {}", id);
        return filmStorage.getFilm(Integer.parseInt(id));
    }

    @ResponseBody
    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @ResponseBody
    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable String id, @PathVariable String userId) {
        log.info("Запрос на добавление лайка от пользователя с ID - {}.", userId);
        filmService.addLike(Integer.parseInt(id), Integer.parseInt(userId));
        return filmService.getFilm(Integer.parseInt(id));
    }

    @ResponseBody
    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable String id, @PathVariable String userId) {
        log.info("Запрос на удаление лайка от пользователя с ID - {}.", userId);
        filmService.deleteLike(Integer.parseInt(id), Integer.parseInt(userId));
        return filmService.getFilm(Integer.parseInt(id));
    }

    @ResponseBody
    @GetMapping("/popular")
    public List<Film> getTopFilm(@RequestParam(defaultValue = "10") String count) {
        log.info("Запрос на получение {} самых популярных фильмов.", count);
        return filmService.getTopFilms(Integer.parseInt(count));
    }
}