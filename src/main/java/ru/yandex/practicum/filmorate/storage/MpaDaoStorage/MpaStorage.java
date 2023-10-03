package ru.yandex.practicum.filmorate.storage.MpaDaoStorage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    public Mpa getMpa(int id);

    public  List<Mpa> getAllMpa();
}
