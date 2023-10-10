package ru.yandex.practicum.filmorate.storage.FilmDaoStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DirectorDaoStorage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.GenreDaoStorage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {

        Film film = new Film(rs.getInt("film_id"),
                rs.getString("fn"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration"),
                new Mpa(
                        rs.getInt("mpa_id"),
                        rs.getString("mn")
                )
        );

        return updateFilmData(film);
    }

    private Film updateFilmData(Film film) {
        int filmId = film.getId();

        LinkedHashSet<Genre> genres = new LinkedHashSet<>(genreStorage.getGenresOfFilm(filmId));
        if (genres.isEmpty()) {
            film.setGenres(new LinkedHashSet<>());
        } else {
            film.setGenres(genres);
        }

        LinkedHashSet<Director> directors = new LinkedHashSet<>(directorStorage.getFilmDirector(filmId));
        if (directors.isEmpty()) {
            film.setDirectors(new LinkedHashSet<>());
        } else {
            film.setDirectors(directors);
        }

        return film;
    }
}
