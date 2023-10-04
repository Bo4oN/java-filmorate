package ru.yandex.practicum.filmorate.storage.DirectorDaoStorage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {
    Director create(Director director);

    Director update(Director director);

    Director get(int id);

    List<Director> getAll();

    void delete(int id);
}
