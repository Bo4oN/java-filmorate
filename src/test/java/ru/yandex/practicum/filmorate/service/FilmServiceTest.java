package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DirectorDaoStorage.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.DirectorDaoStorage.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.DirectorDaoStorage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FeedDBStorage.FeedDBStorage;
import ru.yandex.practicum.filmorate.storage.FeedDBStorage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmDaoStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmDaoStorage.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.FilmDaoStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDaoStorage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDaoStorage.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.GenreDaoStorage.GenreStorage;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@EnableAutoConfiguration(exclude = {BatchAutoConfiguration.class})
@Sql(value = {
        "/sql/directors/create-directors-after.sql",
        "/sql/films/create-films-after.sql",
        "/sql/users/create-users-after.sql",
        "/sql/genres/create-film-genres-after.sql",
        "/sql/genres/create-genres-after.sql"
}, executionPhase = AFTER_TEST_METHOD)
class FilmServiceTest {
    private final DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:/sql/create-directors-schema.sql")
            .addScript("classpath:/sql/create-films-schema.sql")
            .addScript("classpath:/sql/create-users-schema.sql")
            .addScript("classpath:/sql/directors/create-directors-before.sql")
            .addScript("classpath:/sql/films/create-films-before.sql")
            .addScript("classpath:/sql/users/create-users-before.sql")
            .addScript("classpath:/sql/genres/create-genres-before.sql")
            .addScript("classpath:/sql/genres/create-film-genres-before.sql")
            .build();
    private final JdbcTemplate jdbc = new JdbcTemplate(dataSource);
    private final RowMapper<Genre> genreRowMapper = new GenreRowMapper();
    private final RowMapper<Director> directorRowMapper = new DirectorRowMapper();
    private final FeedStorage feedStorage = new FeedDBStorage(jdbc);
    private final GenreStorage genreStorage =
            new GenreDbStorage(
                    jdbc,
                    genreRowMapper
            );
    private final DirectorStorage directorStorage =
            new DirectorDbStorage(
                    jdbc,
                    directorRowMapper
            );
    private final RowMapper<Film> filmRowMapper =
            new FilmRowMapper(
                    genreStorage,
                    directorStorage
            );
    private final FilmStorage filmStorage =
            new FilmDbStorage(
                    jdbc,
                    filmRowMapper,
                    genreStorage,
                    directorStorage,
                    feedStorage
            );
    private final FilmService service = new FilmService(filmStorage);
    private final DirectorService directorService = new DirectorService(directorStorage);

    @Test
    void getDirectorFilmsSortByYear() {
        Director director = directorService.get(1);
        assertEquals("Stanley Kubrick", director.getName());

        List<Film> directorFilms = service.getDirectorFilms(director.getId(), "year");
        assertEquals("Lolita", directorFilms.get(0).getName());
        assertEquals("Eyes Wide Shut", directorFilms.get(7).getName());
    }

    @Test
    void getDirectorFilmsSortByLikes() {
        Director director = directorService.get(1);
        assertEquals("Stanley Kubrick", director.getName());

        List<Film> directorFilms = service.getDirectorFilms(director.getId(), "likes");
        assertEquals("2001: A Space Odyssey", directorFilms.get(0).getName());
        assertEquals("Eyes Wide Shut", directorFilms.get(7).getName());
    }

    void getTopFilmsByGenresAndYear() {
        List<Film> topTenFilmsByGenresAndYear = service.getTopFilms(10, 2, 1980);
        assertEquals(2, topTenFilmsByGenresAndYear.size());
        assertEquals("Raging Bull", topTenFilmsByGenresAndYear.get(0).getName());
        assertEquals("The Shining", topTenFilmsByGenresAndYear.get(1).getName());
    }

    @Test
    void getTopFilms() {
        List<Film> topTenFilms = service.getTopFilms(10, null, null);
        assertEquals(9,topTenFilms.size());
    }
}