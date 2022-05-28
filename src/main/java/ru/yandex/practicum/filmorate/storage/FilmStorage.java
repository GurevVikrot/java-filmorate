package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    boolean saveFilm(Film film);

    boolean updateFilm(Film film);

    Film getFilm(Integer id);

    boolean deleteFilm(Integer id);

    List<Film> getFilms();

    List<Film> getTopFilms(int count);
}
