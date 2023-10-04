package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDaoStorage.DirectorDbStorage;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDbStorage storage;

    public Director create(Director director) {
        return storage.create(director);
    }

    public Director update(Director director) {
        return storage.update(director);
    }

    public Director get(int id) {
        return storage.get(id);
    }

    public List<Director> getAll() {
        return storage.getAll();
    }

    public void delete(int id) {
        storage.delete(id);
    }
}
