package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    String deleteFilm(Integer id);

    Film getFilm(Integer id);

    List<Film> getFilms();

    boolean addLikeToFilm(Integer FilmId, Integer UserId);

    boolean removeLikeFromFilm(Integer FilmId, Integer UserId);

    List<Film> getTopFilms(Integer count);
}