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
import ru.yandex.practicum.filmorate.controllers.ErrorHandler;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDaoStorage.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.DirectorDaoStorage.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.DirectorDaoStorage.DirectorStorage;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@EnableAutoConfiguration(exclude = {BatchAutoConfiguration.class})
@Sql(value = {
        "/sql/directors/create-directors-after.sql"
}, executionPhase = AFTER_TEST_METHOD)
class DirectorServiceTest {
    private final ErrorHandler errors = new ErrorHandler();
    private final DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:/sql/create-directors-schema.sql")
            .addScript("classpath:/sql/directors/create-directors-before.sql")
            .build();
    private final JdbcTemplate jdbc = new JdbcTemplate(dataSource);
    private final RowMapper<Director> rowMapper = new DirectorRowMapper();
    private final DirectorStorage storage = new DirectorDbStorage(jdbc, rowMapper);
    private final DirectorService service = new DirectorService(storage);
    private final List<Director> demoDirectors = List.of(
            new Director(999, "George Lucas"),
            new Director(999, "Peter Robert Jackson"),
            new Director(999, " ")
    );

    @Test
    void create() {
        Director director = service.create(demoDirectors.get(0));
        assertEquals(17, director.getId());
        assertEquals("George Lucas", director.getName());
    }

    @Test
    void createTwice() {
        Director director = demoDirectors.get(0);
        service.create(director);
        try {
            service.create(director);
        } catch (RuntimeException e) {
            assertEquals("A director with that name (George Lucas) already exists", e.getMessage());
        }
    }


    @Test
    void createWithBlankName() {
        Director director = demoDirectors.get(2);
//        service.create(director);
        try {
            service.create(director);
        } catch (RuntimeException e) {
            assertEquals("A director with that name ( ) already exists", e.getMessage());
        }
    }

    @Test
    void updateOliverStoneToPeterRobertJackson() {
        Director director = demoDirectors.get(1);
        Director directorNew = new Director(14, director.getName());

        assertEquals("Oliver Stone", service.get(14).getName());

        Director directorUpdate = service.update(directorNew);
        assertEquals("Peter Robert Jackson", directorUpdate.getName());
    }

    @Test
    void updateFailId() {
        try {
            service.update(demoDirectors.get(1));
        } catch (NotFoundException e) {
            assertEquals("Director with ID:999 not found", errors.handleNotFoundException(e).getError());
        }

    }

    @Test
    void get() {
        assertEquals("Stanley Kubrick", service.get(1).getName());
        assertEquals("Charles Chaplin", service.get(4).getName());
        assertEquals("Francis Ford Coppola", service.get(8).getName());
        assertEquals("Ridley Scott", service.get(15).getName());
    }

    @Test
    void getAll() {
        assertEquals(16, service.getAll().size());
    }

    @Test
    void delete() {
        assertEquals(16, service.getAll().size());
        service.delete(1);
        try {
            service.get(1);
        } catch (NotFoundException e) {
            assertEquals("Director with ID:1 not found", e.getMessage());
        }
        assertEquals(15, service.getAll().size());
    }
}