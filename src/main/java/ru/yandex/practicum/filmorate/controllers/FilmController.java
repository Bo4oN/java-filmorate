package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
@RestController
public class FilmController {

    private final FilmService filmService;
    private final UserService userService;

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
    public Film getFilmById(@PathVariable int id) {
        log.info("Получен запрос на получения фильма с id - {}", id);
        return filmService.getFilm(id);
    }

    @ResponseBody
    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable int id) {
        log.info("Получен запрос на удаление фильма с id - {}", id);
        filmService.deleteFilm(id);
    }

    @ResponseBody
    @GetMapping()
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @ResponseBody
    @PutMapping("/{id}/like/{userId}")
    public Entity addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос на добавление лайка от пользователя с ID - {}.", userId);
        filmService.addLike(id, userId);
        return filmService.getFilm(id);
    }

    @ResponseBody
    @DeleteMapping("/{id}/like/{userId}")
    public Entity deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос на удаление лайка от пользователя с ID - {}.", userId);
        filmService.deleteLike(id, userId);
        userService.getUser(userId);
        return filmService.getFilm(id);
    }

    @ResponseBody
    @GetMapping("/popular")
    public List<Film> getTopTenFilm(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрос на получение {} самых популярных фильмов.", count);
        return filmService.getTopFilms(count);
    }

    @ResponseBody
    @GetMapping("/popular/{count}")
    public List<Film> getTopFilm(@PathVariable int count) {
        log.info("Запрос на получение {} самых популярных фильмов.", count);
        return filmService.getTopFilms(count);
    }

    /**
     * Возвращает список фильмов режиссера
     * отсортированных по количеству лайков или году выпуска.
     *
     * @param sortBy sortBy=[year,likes]
     * @return список фильмов режиссера
     */
    @RequestMapping("/director/{directorId}")
    @GetMapping
    public List<Film> getDirectorFilms(
            @PathVariable int directorId,
            @RequestParam(name = "sortBy", defaultValue = "none") String sortBy) {

        return filmService.getDirectorFilms(directorId, sortBy);
    }
}