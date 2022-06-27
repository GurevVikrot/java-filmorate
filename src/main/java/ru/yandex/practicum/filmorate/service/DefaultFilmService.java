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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DefaultFilmService implements FilmService {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    protected final FilmStorage filmStorage;
    protected final UserStorage userStorage;

    @Autowired
    public DefaultFilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film createFilm(Film film) {
        checkDateFilm(film);
        checkNonexistentId(film);
        checkSpace(film);
        Optional<Film> filmOptional = filmStorage.saveFilm(film);
        if (filmOptional.isPresent()) {
            log.info("film: {} успешно создан", film);
            return filmOptional.get();
        }

        log.info("При добавлении фильма в хранилище что-то пошло не так, film: {}", film);
        throw new StorageException("Ошибка добавления фильма");
    }

    @Override
    public Film updateFilm(Film film) {
        checkSpace(film);
        checkDateFilm(film);

        if (film.getId() >= 0 && filmStorage.updateFilm(film).isPresent()) {

            log.info("film успешно обновлен: {}", film);
            Film updatedFilm = getFromStorage(film.getId());

            // Проверка из-за теста Film update remove genre где требуется получить пустой массив
            // При этом остальные тесты проверяют на null
            if (film.getGenres() != null && film.getGenres().isEmpty()) {
                updatedFilm.setGenres(new LinkedHashSet<>());
            }

            return updatedFilm;
        }

        log.warn("Ошибка обновления фильма, фильма не существует: {}", film);
        throw new StorageException("Обновление невозможно, фильма не существует или передан id неверного формата");
    }

    @Override
    public String deleteFilm(long id) {
        if (filmStorage.deleteFilm(id)) {
            log.info("film c id = {} удален", id);
            return "Фильм удален";
        }

        log.info("Ошибка удаления фильма, фильма с id = {} не существует", id);
        throw new StorageException("Удаление невозможно, фильма не существует или передан id неверного формата");
    }

    @Override
    public Film getFilm(long id) {
        return getFromStorage(id);
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public boolean addLikeToFilm(long filmId, long userId) {
        Film film = getFromStorage(filmId);

        if (userStorage.userExist(userId) && film.addLike(userId)) {
            log.info("Добавлен like фильму {} от пользователя с id = {}", filmId, userId);
            return true;
        }

        log.warn("Ошибка добавления like фильму. Он уже есть или фильма id = {} и/или пользователя id = {} не существует",
                filmId, userId);
        throw new StorageException("Невозможно поставить like");
    }

    @Override
    public boolean removeLikeFromFilm(long filmId, long userId) {
        Film film = getFromStorage(filmId);

        if (userStorage.userExist(userId) && film.deleteLike(userId)) {
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

    private void checkNonexistentId(Film film) {
        if (film.getId() > 0) {
            log.warn("Ошибка создания фильма, получен фильм с изначально заданным id");
            throw new ValidationException("Ошибка создания фильма, неверный формат id");
        }
    }

    private void checkSpace(Film film) {
        if (film.getName().startsWith(" ")) {
            film.setName(film.getName().trim());
        }
        if (film.getDescription().startsWith(" ")) {
            film.setDescription(film.getDescription().trim());
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

    private Film getFromStorage(Long id) {
        return filmStorage.getFilm(id).orElseThrow(() -> new StorageException("Фильма не существует",
                "Ошибка получения фильма id = {} из хранилища, возращен null"));
    }
}