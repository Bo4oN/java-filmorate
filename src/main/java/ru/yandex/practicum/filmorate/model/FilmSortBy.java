package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

import java.util.Map;

/**
 * Класс предназначен для сортировки поиска фильмов заданного режиссёра
 * по году релиза, или по лайкам, или просто по фильмам

 * @see ru.yandex.practicum.filmorate.storage.FilmDaoStorage.FilmDbStorage#getFilmDirector(int, FilmSortBy)
 */
@Getter
public enum FilmSortBy {
    YEAR(Map.of(
            "SELECT", " EXTRACT(YEAR FROM CAST (F.release_date as DATE)) YEAR_RELEASE ",
            "LEFT JOIN", "",
            "ORDER BY", " ORDER BY YEAR_RELEASE ASC")
    ),
    LIKES(Map.of(
            "SELECT", " COUNT(L.like_id) FILM_LIKES ",
            "LEFT JOIN", " LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID ",
            "ORDER BY", " ORDER BY FILM_LIKES ")
    ),
    NONE(Map.of(
            "SELECT", "",
            "LEFT JOIN", "",
            "ORDER BY", "")
    );

    private final Map<String, String> params;

    FilmSortBy(Map<String, String> params) {
        this.params = params;
    }

}
