package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryStorage<T extends Entity> implements Storage<T> {

    private final HashMap<Integer, T> films = new HashMap<>();
    private final HashMap<Integer, T> users = new HashMap<>();
    private int nextIdFilm = 1;
    private int nextIdUser = 1;

    @Override
    public T add(T data) {
        if (data.getClass() == Film.class) {
            data.setId(nextIdFilm++);
            films.put(data.getId(), data);
        } else if (data.getClass() == User.class) {
            data.setId(nextIdUser++);
            users.put(data.getId(), data);
        }
        return data;
    }

    @Override
    public T update(T data) {
        if (films.containsKey(data.getId())) {
            films.put(data.getId(), data);
        } else if (users.containsKey(data.getId())) {
            users.put(data.getId(), data);
        } else {
            log.debug("Не найден элемент для обновления, его ID - '{}'", data.getId());
            throw new NotFoundException("Нет элемента с ID - " + data.getId());
        }
        return data;
    }

    @Override
    public T delete(int id) {
        if (films.containsKey(id)) {
            return films.remove(id);
        } else if (users.containsKey(id)) {
            return users.remove(id);
        } else {
            throw new NotFoundException("Нет данных по ID - " + id);
        }
    }

    @Override
    public T get(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Нет данных по ID - " + id);
        }
    }

    @Override
    public List<T> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<T> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
