package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDaoStorage.DirectorStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage storage;

    /**
     * Создаёт запись нового режиссёра

     * @param director новая запись, достаточно имени
     * @return новая запись со своим новым идентификатором
     */
    public Director create(Director director) {
        checkName(director);

        director = storage.create(director);
        log.info("CREATE director:{}", director);
        return director;
    }

    /**
     * Изменяет данные нужного режиссёра

     * @param director новые данные
     * @return изменённые данные режиссёра
     */
    public Director update(Director director) {
        isExist(director.getId());

        checkName(director);

        director = storage.update(director);
        log.info("UPDATE director:{}", director);
        return director;
    }

    /**
     * Получает нужного режиссёра

     * @param id идентификатор режиссёра
     * @return режиссёр
     */
    public Director get(int id) {
        Director director = storage.get(id);
        log.info("GET director:{}", director);
        return director;
    }

    /**
     * Поиск режиссёра по точному имени
     *
     * @param name Имя, которое нужно найти
     */
    public void find(String name) {
        Director director = storage.find(name);
        log.info("GET director:{}", director);
    }

    /**
     *  Получает список всех режиссёров

     * @return Список режиссёров
     */
    public List<Director> getAll() {
        List<Director> directors = storage.getAll();
        if (directors == null) {
            directors = new ArrayList<>();
        }
        log.info("GET ALL {} directors", directors.size());
        return directors;
    }

    /**
     * Удаляет режиссёра

     * @param id идентификатор режиссёра
     */
    public void delete(int id) {
        log.info("DELETE director ID:{}", id);
        storage.delete(id);
    }

    private void isExist(int id) {
        get(id);
    }

    /**
     * Проверка имени режиссёра

     * @param director режиссёр
     */
    private void checkName(Director director) {
        String name = director.getName();

        if (isInvalidName(name)) {
            String error = String.format("A director with that name (%s) is not valid", name);
            log.error(error);
            throw new ValidationException(error);
        }

        if (isUniqueName(name)) {
            String error = String.format("A director with that name (%s) already exists", name);
            log.error(error);
            throw new ValidationException(error);
        }
    }

    /**
     * Проверка имени на здравый смысл

     * @param name Имя для проверки
     * @return true - something wrong; false - OK;
     */
    private boolean isInvalidName(String name) {
        name = name.trim();
        log.info("Director.Name after trim: {}",name);
        return name.length() == 0;
    }

    /**
     * Проверка имени на уникальность в базе

     * @param name Имя для проверки
     * @return true - something wrong; false - OK;
     */
    private boolean isUniqueName(String name) {
        try {
            find(name);
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}
