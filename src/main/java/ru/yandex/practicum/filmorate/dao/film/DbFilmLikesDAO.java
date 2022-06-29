package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbFilmLikesDAO implements LikesDAO {
    private final JdbcTemplate jdbcTemplate;

    public DbFilmLikesDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addLike(long filmId, long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id)" +
                "VALUES(?, ?)";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    @Override
    public boolean removeLike(long filmId, long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    @Override
    public List<Long> getFilmLikes(long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, filmId);
    }

    @Override
    public List<Long> getTopFilms(int count) {
        String sql = "SELECT film_id FROM film_likes GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, Long.class, count);
    }
}