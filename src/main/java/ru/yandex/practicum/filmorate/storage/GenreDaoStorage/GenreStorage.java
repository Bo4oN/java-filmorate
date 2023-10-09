package ru.yandex.practicum.filmorate.storage.GenreDaoStorage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    Genre getGenre(int id);

    List<Genre> getAllGenre();

    void addFilmToGenre(Film film);

    Set<Genre> getGenresOfFilm(int filmId);

    void updateFilmToGenre(Film film);

    void deleteFilmToGenre(Film film);
}
