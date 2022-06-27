package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> saveFilm(Film film);

    Optional<Film> updateFilm(Film film);

    Optional<Film> getFilm(long id);

    boolean deleteFilm(long id);

    List<Film> getFilms();

    List<Film> getTopFilms(int count);
}
