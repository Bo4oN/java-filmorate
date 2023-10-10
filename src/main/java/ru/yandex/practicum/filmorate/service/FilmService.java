package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;
import ru.yandex.practicum.filmorate.storage.FilmDaoStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserDaoStorage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;
    private static final LocalDate BIRTHDAY_MOVIE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage storage, UserStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        validationFilm(film);
        return storage.add(film);
    }

    public Film updateFilm(Film film) {
        validationFilm(film);
        return storage.update(film);
    }

    public Film getFilm(int id) {
        return storage.get(id);
    }

    public void deleteFilm(int id) {
        storage.deleteFilm(id);
    }

    public List<Film> getAllFilms() {
        return storage.getAll();
    }

    public void addLike(int filmId, int userId) {
        storage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        storage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        log.info("[i] Incoming params in getTopFilms(count, genreId, year):\n"
                + " Count:{}\n"
                + " GenreId:{}\n"
                + " Year:{}", count, genreId, year);

        return storage.getTopFilms(count, genreId, year);
    }

    public List<Film> getDirectorFilms(int directorId, String sortBy) {
        List<Film> directorFilms = storage.getFilmDirector(
                directorId,
                FilmSortBy.valueOf(sortBy.toUpperCase())
        );

        if (directorFilms.isEmpty()) {
            String error = String.format("Director with ID:%d not found", directorId);
            log.error(error);
            throw new NotFoundException(error);
        }

        log.info("Director Films\n{}", directorFilms);
        return directorFilms;
    }

    public List<Film> searchFilms(String query, String by) {
        return storage.searchFilms(query, by);
    }

    private void validationFilm(Film film) {
        if (film.getReleaseDate().isBefore(BIRTHDAY_MOVIE)) {
            log.debug("Не валидная дата премьеры.");
            throw new ValidationException("Дата премьеры фильма не может быть раньше 28 декабря 1895г.");
        }
    }

    public List<Film> getCommonTopFilm(int userId, int friendId) {
        if (userId == friendId) {
            log.error("Указан один и тот же идентификатор");
            throw new RuntimeException("Указан один и тот же идентификатор");
        }
        userStorage.get(userId);
        userStorage.get(friendId);
        return storage.getCommonTopFilm(userId, friendId);
    }
}
