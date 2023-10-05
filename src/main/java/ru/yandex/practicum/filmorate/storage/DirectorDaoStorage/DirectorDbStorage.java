package ru.yandex.practicum.filmorate.storage.DirectorDaoStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Director> rowMapper;

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", director.getName());

        int directorId = simpleJdbcInsert.executeAndReturnKey(params).intValue();

        return get(directorId);
    }

    @Override
    public Director update(Director director) {
        int directorId = director.getId();
        String directorName = director.getName();
        String sqlQuery = "UPDATE DIRECTORS SET name = ? WHERE id = ?";

        int rowsCount = jdbcTemplate.update(sqlQuery, directorName, directorId);

        if (!(rowsCount > 0)) {
            String error = String.format("Director with ID:%d not found", directorId);
            log.error(error);
            throw new NotFoundException(error);
        }

        log.info("Director with ID:{} changed.", directorId);
        return get(directorId);
    }

    /**
     * get director by id
     *
     * @param id the ID of the director
     * @return director
     */
    @Override
    public Director get(int id) {
        String error = String.format("Director with ID:%d not found", id);
        String sql = "SELECT D.id, D.name"
                + " FROM DIRECTORS D"
                + " WHERE D.id = ?";

        return jdbcTemplate.query(sql, rowMapper, id)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException(error));
    }

    @Override
    public Director find(String name) {
        String error = String.format("Director with NAME:%s not found", name);
        String sql = "SELECT id, name"
                + " FROM DIRECTORS"
                + " WHERE name = ?";
        return jdbcTemplate.query(sql, rowMapper,name)
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException(error));
    }

    /**
     * get all directors
     *
     * @return list of directors
     */
    @Override
    public List<Director> getAll() {
        String sql = "SELECT id, name"
                + " FROM DIRECTORS"
                + " ORDER BY id";
        return jdbcTemplate.query(sql, rowMapper);
    }


    /**
     * delete director
     *
     * @param id the ID of the director
     */
    @Override
    public void delete(int id) {
        String error = String.format("Director with ID:%d not found", id);
        String sql = "DELETE FROM DIRECTORS WHERE id = ?";
        int rowCount = jdbcTemplate.update(sql, id);
        if (rowCount > 0) {
            log.info("Director with ID:{} removed.", id);
        } else {
            log.error(error);
            throw new NotFoundException(error);
        }
    }

    /**
     * add film directors
     *
     * @param film is a movie in which you need to add directors
     */
    @Override
    public void addFilmDirector(Film film) {
        String sqlQuery = "INSERT INTO FILMS_DIRECTOR (director_id, film_id) VALUES ( ?, ?)";
        List<Director> filmDirectors = new ArrayList<>(film.getDirectors());
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmDirectors.get(i).getId());
                ps.setInt(2, film.getId());
            }

            @Override
            public int getBatchSize() {
                return filmDirectors.size();
            }
        });
    }

    /**
     * update film directors
     *
     * @param film is the film in which you need to update the directors
     */
    @Override
    public void updateFilmDirector(Film film) {
        String sqlQuery = "DELETE FROM FILMS_DIRECTOR WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        addFilmDirector(film);
    }

    /**
     * get all the film directors
     *
     * @param filmId id of film
     * @return all directors of film
     */
    @Override
    public Set<Director> getFilmDirector(int filmId) {
        String sqlQuery = "SELECT * FROM DIRECTORS D"
                + " WHERE D.id IN ("
                + "     SELECT DISTINCT FD.director_id FROM FILMS_DIRECTOR FD"
                + "     WHERE FD.film_id = ?"
                + ");";
        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, rowMapper, filmId));
    }


}
