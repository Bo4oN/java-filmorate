package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class InMemoryStorage<T extends Entity> implements Storage<T> {

    private final HashMap<Integer, T> storage = new HashMap<>();
    private int nextId = 1;

    @Override
    public T add(T data) {
        if (!storage.containsKey(data.getId())) {
            data.setId(nextId++);
            storage.put(data.getId(), data);
        } else {
            throw new ValidationException("Данные уже добавлены");
        }
        return data;
    }

    @Override
    public T update(T data) {
        if (storage.containsKey(data.getId())) {
            storage.put(data.getId(), data);
        } else {
            log.debug("Не найден элемент для обновления, его ID - '{}'", data.getId());
            throw new NotFoundException("Нет элемента с ID - " + data.getId());
        }
        return data;
    }

    @Override
    public T delete(int id) {
        if (storage.containsKey(id)) {
            return storage.remove(id);
        } else {
            throw new NotFoundException("Нет данных по ID - " + id);
        }
    }

    @Override
    public T get(int id) {
        if (storage.containsKey(id)) {
            return storage.get(id);
        } else {
            throw new NotFoundException("Нет данных по ID - " + id);
        }
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(storage.values());
    }
}
