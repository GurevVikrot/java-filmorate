package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.film.LikesDAO;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.StorageException;

@Component
@Primary
@Slf4j
public class DbFilmService extends DefaultFilmService {
    private final LikesDAO likesDAO;

    @Autowired
    public DbFilmService(FilmStorage filmStorage, UserStorage userStorage, LikesDAO likesDAO) {
        super(filmStorage, userStorage);
        this.likesDAO = likesDAO;
    }

    @Override
    public boolean addLikeToFilm(long filmId, long userId) {
        Film film = super.getFilm(filmId);

        if (userStorage.userExist(userId) && film.addLike(userId)) {
            likesDAO.addLike(filmId, userId);
            log.info("Добавлен like фильму {} от пользователя с id = {}", filmId, userId);
            return true;
        }

        log.warn("Ошибка добавления like фильму. Он уже есть или фильма id = {} и/или пользователя id = {} не существует",
                filmId, userId);
        throw new StorageException("Невозможно поставить like");
    }

    @Override
    public boolean removeLikeFromFilm(long filmId, long userId) {
        Film film = super.getFilm(filmId);

        if (userStorage.userExist(userId) && film.deleteLike(userId)) {
            likesDAO.removeLike(filmId, userId);
            log.info("Удален like у фильма id = {} от пользователя с id = {}", filmId, userId);
            return true;
        }

        log.warn("Ошибка удаления like фильма. Отсутствует like или фильма id = {} и/или пользователя id = {} не существует.",
                filmId, userId);
        throw new StorageException("Невозможно удалить like");
    }
}