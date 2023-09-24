package ru.yandex.practicum.filmorate.storage.GenreDaoStorage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    public Genre getGenre(int id);

    public List<Genre> getAllGenre();

    public void addFilmToGenre(Film film);

    public Set<Genre> getGenresOfFilm(int filmId);

    public void updateFilmToGenre(Film film);
}
