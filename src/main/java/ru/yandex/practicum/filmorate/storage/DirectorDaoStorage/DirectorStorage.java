package ru.yandex.practicum.filmorate.storage.DirectorDaoStorage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {
    Director create(Director director);

    Director update(Director director);

    Director get(int id);

    Director find(String name);

    List<Director> getAll();

    void delete(int id);

    void addFilmDirector(Film film);

    void updateFilmDirector(Film film);

    void deleteFilmDirector(Film film);

    Set<Director> getFilmDirector(int filmId);
}
