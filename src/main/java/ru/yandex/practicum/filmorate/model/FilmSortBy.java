package ru.yandex.practicum.filmorate.model;

import java.util.Map;

/**
 * Класс предназначен для сортировки поиска фильмов заданного режиссёра
 * по году релиза, или по лайкам, или просто по фильмам

 * @see ru.yandex.practicum.filmorate.storage.FilmDaoStorage.FilmDbStorage#getFilmDirector(int, FilmSortBy)
 */
public enum FilmSortBy {
    YEAR(Map.of(
            "SELECT", " EXTRACT(YEAR FROM TIMESTAMP F.release_date) YEAR_RELEASE ",
            "ORDER BY", " ORDER BY YEAR_RELEASE DESC ")
    ),
    LIKES(Map.of(
            "SELECT", " COUNT(L.like_id) FILM_LIKES ",
            "ORDER BY", " ORDER BY film_likes DESC ")
    ),
    NONE(Map.of(
            "SELECT", "",
            "ORDER BY", "")
    );

    private final Map<String, String> params;

    FilmSortBy(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
