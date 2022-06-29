package ru.yandex.practicum.filmorate.dao.film;

import java.util.List;

public interface LikesDAO {
    boolean addLike(long filmId, long userId);

    boolean removeLike(long filmId, long userId);

    List<Long> getFilmLikes(long filmId);

    List<Long> getTopFilms(int count);
}