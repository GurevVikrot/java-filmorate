package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class DbFilmGenreDAO implements FilmGenreDAO {
    private final JdbcTemplate jdbcTemplate;

    public DbFilmGenreDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Set<Genre> getGenres(long filmId) {
        String sql = "SELECT fg.genre_id, g.genre FROM film_genre AS fg " +
                "INNER JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "WHERE film_id = ?" +
                "ORDER BY fg.genre_id";
        SqlRowSet genreRowSet = jdbcTemplate.queryForRowSet(sql, filmId);
        if (genreRowSet.next()) {
            genreRowSet.previous();
            Set<Genre> genres = new LinkedHashSet<>();

            while (genreRowSet.next()) {
                genres.add(new Genre(genreRowSet.getInt("genre_id"), genreRowSet.getString("genre")));
            }

            return genres;
        }
        return null;
    }

    @Override
    public boolean removeGenre(long id) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public boolean updateGenre(long filmId, Set<Genre> prevGenreId, Set<Genre> genres) {
        removeGenre(filmId);
        return setGenres(filmId, genres);
    }

    @Override
    public boolean setGenres(long filmId, Set<Genre> genres) {
        String sql = "INSERT INTO film_genre (film_id, genre_id)" +
                "VALUES (?, ?)";

        for (Genre genre : genres) {
            jdbcTemplate.update(sql, filmId, genre.getId());
        }

        return true;
    }
}