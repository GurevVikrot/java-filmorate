package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GenreDAO {
    private final JdbcTemplate jdbcTemplate;

    public GenreDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, (rs) -> {
            List<Genre> genres = new ArrayList<>();

            while (rs.next()) {
                genres.add(genreFromBd(rs));
            }

            return genres;
        });
    }

    public Optional<Genre> getGenre(int genreId) {
        String sql = "SELECT genre_id, genre FROM genre WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> genreFromBd(rs), genreId);
        if (genres.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(genres.get(0));
    }

    private Genre genreFromBd(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre"));
    }
}
