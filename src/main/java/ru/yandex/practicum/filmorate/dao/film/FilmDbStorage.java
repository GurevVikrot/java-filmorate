package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.util.StorageException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO класс для работы с таблицей films
 */

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDAO filmGenreDAO;
    private final LikesDAO likesDAO;
    private final RatingMpaDAO ratingMpaDAO;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmGenreDAO filmGenreDAO, LikesDAO likesDAO, RatingMpaDAO ratingMpaDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreDAO = filmGenreDAO;
        this.likesDAO = likesDAO;
        this.ratingMpaDAO = ratingMpaDAO;
    }

    @Override
    public Optional<Film> saveFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long filmId = simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).longValue();

        if (film.getGenres() != null) {
            filmGenreDAO.setGenres(filmId, film.getGenres());
        }

        return getFilm(filmId);
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?," +
                "rating = ?, duration = ? WHERE film_id = ?";

        if (film == null) {
            throw new StorageException("Получен null для обновления");
        }

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getDuration(),
                film.getId());

        // Достаем предыдущий жанр
        Set<Genre> prevGenreSet = filmGenreDAO.getGenres(film.getId());

        // Если у фильма задан жанр - добавляем в связующую таблицу
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenreDAO.updateGenre(film.getId(), prevGenreSet, film.getGenres());
        } // Иначе если жанры у фильма отсутствуют, удаляем предыдущие связи с жанрами
        else if (prevGenreSet != null && !prevGenreSet.isEmpty()) {
            filmGenreDAO.removeGenre(film.getId());
        }

        return getFilm(film.getId());
    }

    @Override
    public Optional<Film> getFilm(long id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> filmFromDb(rs), id);
        if (films.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.query(sql, (rs, rowNum) -> filmFromDb(rs), id).get(0));
    }

    @Override
    public boolean deleteFilm(long id) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> filmFromDb(rs));
    }

    @Override
    public List<Film> getTopFilms(int count) {
        List<Long> filmsId = likesDAO.getTopFilms(count);

        if (filmsId.isEmpty()) {
            return getFilms().stream()
                    .limit(count)
                    .collect(Collectors.toList());
        }

        return filmsId.stream()
                .map(this::getFilm)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    // Преобразование Film в Map для SimpleJdbcInsert
    private Map<String, Object> filmToMap(Film film) {
        if (film == null) {
            throw new StorageException("Получен null для сохранения");
        }

        Map<String, Object> filmMap = new HashMap<>();
        filmMap.put("name", film.getName());
        filmMap.put("description", film.getDescription());
        filmMap.put("rating", film.getMpa().getId());
        filmMap.put("release_date", film.getReleaseDate());
        filmMap.put("duration", film.getDuration());
        return filmMap;
    }

    private Film filmFromDb(ResultSet rs) throws SQLException {
        Film film = new Film(rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"));
        film.setId(rs.getLong("film_id"));

        // Задаем фильму жанры
        Set<Genre> genres = filmGenreDAO.getGenres(film.getId());
        if (genres != null) {
            film.setGenres(genres);
        }

        // Задаем фильму рейтинг
        RatingMpa filmRating = ratingMpaDAO.getRatingById(rs.getInt("rating")).orElse(null);
        film.setMpa(filmRating);

        // Добавляем лайки
        for (long userId : likesDAO.getFilmLikes(film.getId())) {
            film.addLike(userId);
        }

        return film;
    }
}
