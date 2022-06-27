package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RatingMpaDAO {
    private final JdbcTemplate jdbcTemplate;

    public RatingMpaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RatingMpa> getAllRatings() {
        String sql = "SELECT * FROM rating_mpa";
        return jdbcTemplate.query(sql, (rs) -> {
            List<RatingMpa> rating = new ArrayList<>();

            while (rs.next()) {
                rating.add(ratingFromBd(rs));
            }

            return rating;
        });
    }

    public Optional<RatingMpa> getRatingById(int ratingId) {
        String sql = "SELECT rating_id, rating FROM rating_mpa WHERE rating_id = ?";
        List<RatingMpa> ratings = jdbcTemplate.query(sql, (rs, rowNum) -> ratingFromBd(rs), ratingId);
        if (ratings.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ratings.get(0));
    }

    private RatingMpa ratingFromBd(ResultSet rs) throws SQLException {
        return new RatingMpa(rs.getInt("rating_id"), rs.getString("rating"));
    }
}
