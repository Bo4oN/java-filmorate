package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Entity;

import java.util.List;

public interface Storage<T extends Entity> {
    T add(T data);

    T update(T data);

    T delete(int id);

    T get(int id);

    List<T> getAllFilms();

    List<T> getAllUsers();
}
