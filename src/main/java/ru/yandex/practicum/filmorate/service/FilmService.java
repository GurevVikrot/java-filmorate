package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    String deleteFilm(long id);

    Film getFilm(long id);

    List<Film> getFilms();

    boolean addLikeToFilm(long FilmId, long UserId);

    boolean removeLikeFromFilm(long FilmId, long UserId);

    List<Film> getTopFilms(Integer count);
}