package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate BIRTHDAY_MOVIE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        validationFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validationFilm(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getFilms();
    }

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        if (film.getLikes().contains(user)) {
            log.debug("Пользователь с ID - {}, уже ставил лайк фильму с ID - {}.", userId, filmId);
            throw new RuntimeException("Пользователь может поставить только один лайк");
        }
        film.addLike(user);
        return film;
    }

    public Film deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        film.deleteLike(user);
        return film;
    }

    public List<Film> getTopFilms(int count) {
        List<Film> list = filmStorage.getFilms();
        Collections.sort(list, Comparator.comparingInt(o -> o.getLikes().size()));
        List<Film> topList = new ArrayList<>();
        if (count <= list.size()) {
            for (int i = 0; i < count; i++) {
                topList.add(list.get((list.size() - 1) - i));
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                    topList.add(list.get((list.size() - 1) - i));
            }
        }
        return topList;
    }

    private void validationFilm(Film film) {
        if (film.getReleaseDate().isBefore(BIRTHDAY_MOVIE)) {
            log.debug("Не валидная дата премьеры.");
            throw new ValidationException("Дата премьеры фильма не может быть раньше 28 декабря 1985г.");
        }
    }
}
