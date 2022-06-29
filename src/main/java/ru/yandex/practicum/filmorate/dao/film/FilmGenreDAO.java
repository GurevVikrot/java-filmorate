package ru.yandex.practicum.filmorate.dao.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenreDAO {
    boolean setGenres(long filmId, Set<Genre> genres);

    boolean updateGenre(long filmId, Set<Genre> prevGenreId, Set<Genre> genres);

    Set<Genre> getGenres(long filmId);

    boolean removeGenre(long id);
}