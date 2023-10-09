package ru.yandex.practicum.filmorate.storage.FilmDaoStorage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Film get(int id);

    void deleteFilm(int id);

    List<Film> getAll();

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> getTopFilms(int count);

    List<Film> getFilmsOfGenre(Genre genre);
}
