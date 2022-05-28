package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.StorageException;
import ru.yandex.practicum.filmorate.util.ValidationException;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class DefaultFilmService implements FilmService {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private static int idCounter = 1;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public DefaultFilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film createFilm(Film film) {
        checkDateFilm(film);
        checkAndSetId(film);
        checkSpace(film);

        if (filmStorage.saveFilm(film)) {
            log.info("film: {} успешно создан", film);
            return filmStorage.getFilm(film.getId());
        }

        log.info("При добавлении фильма в хранилище что-то пошло не так, film: {}", film);
        throw new StorageException("Ошибка добавления фильма");
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.warn("Не возможно обновить фильм, id = null");
            throw new ValidationException("Не возможно обновить фильм, id = null");
        }

        checkSpace(film);
        checkDateFilm(film);

        if (filmStorage.updateFilm(film)) {
            log.info("film успешно обновлен: {}", film);
            return filmStorage.getFilm(film.getId());
        }

        log.warn("Ошибка обновления фильма, фильма не существует: {}", film);
        throw new StorageException("Обновление невозможно, фильма не существует или передан id неверного формата");
    }

    @Override
    public String deleteFilm(Integer id) {
        if (filmStorage.deleteFilm(id)) {
            log.info("film c id = {} удален", id);
            return "Фильм удален";
        }

        log.info("Ошибка удаления фильма, фильма с id = {} не существует", id);
        throw new StorageException("Удаление невозможно, фильма не существует или передан id неверного формата");
    }

    @Override
    public Film getFilm(Integer id) {
        Film film = filmStorage.getFilm(id);

        if (film == null) {
            log.info("Ошибка получения фильма, фильма с id = {} не существует", id);
            throw new StorageException("Фильма не существует или передан id неверного формата");
        }

        return film;
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public boolean addLikeToFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilm(filmId);

        if (film == null) {
            log.warn("Ошибка добавления like фильму, фильма не существует в коллекции");
            throw new StorageException("Невозможно поставить like несуществующему фильму");
        }

        if (userStorage.userExist(userId) && film.addLike(userId)) {
            filmStorage.updateFilm(film);
            log.info("Добавлен like фильму {} от пользователя с id = {}", filmId, userId);
            return true;
        }

        log.warn("Ошибка добавления like фильму. Он уже есть или фильма id = {} и/или пользователя id = {} не существует",
                filmId, userId);
        throw new StorageException("Невозможно поставить like");
    }

    @Override
    public boolean removeLikeFromFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            log.warn("Ошибка удаления like фильма, фильма не существует в коллекции");
            throw new StorageException("Невозможно удалить like у несуществующего фильма");
        }
        if (userStorage.userExist(userId) && film.deleteLike(userId)) {
            filmStorage.updateFilm(film);
            log.info("Удален like у фильма id = {} от пользователя с id = {}", filmId, userId);
            return true;
        }

        log.warn("Ошибка удаления like фильма. Отсутствует like или фильма id = {} и/или пользователя id = {} не существует.",
                filmId, userId);
        throw new StorageException("Невозможно удалить like");
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
    }

    private void checkAndSetId(Film film) {
        if (film.getId() == null) {
            film.setId(idCounter++);
        } else if (film.getId() >= 0) {
            log.warn("Ошибка создания фильма, получен фильм с изначально заданным id");
            throw new ValidationException("Ошибка создания фильма, неверный формат id");
        }
    }

    private void checkSpace(Film film) {
        if (film.getName().startsWith(" ")) {
            log.warn("Ошибка создания фильма, название начинается с ' '");
            throw new ValidationException("Ошибка создания фильма, название не может начинаться с пробела");
        } else if (film.getDescription().startsWith(" ")) {
            log.warn("Ошибка создания фильма, описание начинается с ' '");
            throw new ValidationException("Ошибка создания фильма, описание не может начинаться с пробела");
        } else {
            film.setName(film.getName().trim());
            film.setDescription(film.getDescription().trim());
        }
    }

    private void checkDateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("Время выпуска фильма меньше минимальной даты: 28-12-1895 film: {}", film);
            throw new ValidationException("Время выпуска фильма меньше минимальной даты: 28-12-1895");
        }
    }
}