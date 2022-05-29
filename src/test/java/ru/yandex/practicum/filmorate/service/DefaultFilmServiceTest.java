package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.util.StorageException;
import ru.yandex.practicum.filmorate.util.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultFilmServiceTest {
    private static InMemoryUserStorage userStorage;
    private static User user;
    private DefaultFilmService filmService;
    private Film film;

    @BeforeAll
    static void beforeAll() {
        userStorage = new InMemoryUserStorage();
        user = new User("a@mail.ru", "login", "name", LocalDate.of(2000, 12, 12));
        user.setId(0);
        userStorage.createUser(user);
    }

    @BeforeEach
    void beforeEach() {
        filmService = new DefaultFilmService(new InMemoryFilmStorage(), userStorage);
        film = new Film("film", "description", LocalDate.of(2000, 12, 12), 100);
    }

    @Test
    void correctCreateFilmTest() {
        filmService.createFilm(film);
        assertFalse(filmService.getFilms().isEmpty());
        assertNotNull(filmService.getFilms().get(0));
    }

    @Test
    void createFilmWithId() {
        filmService.createFilm(film);
        Film filmWithId = new Film("Incorrect", "Date", LocalDate.of(2000, 12, 27), 100);
        filmWithId.setId(filmService.getFilms().get(0).getId());
        assertThrows(ValidationException.class, () -> filmService.createFilm(filmWithId));
    }

    @Test
    void filmWithIncorrectDate() {
        filmService.createFilm(film);
        Film filmWithPastDate = new Film("Incorrect", "Date", LocalDate.of(1895, 12, 27), 100);
        assertThrows(ValidationException.class, () -> filmService.createFilm(filmWithPastDate));

        filmWithPastDate.setId(filmService.getFilms().get(0).getId());
        assertThrows(ValidationException.class, () -> filmService.updateFilm(filmWithPastDate));
    }

    @Test
    void correctUpdateTest() {
        filmService.createFilm(film);
        Film filmToUpdate = new Film("Up", "Date", LocalDate.of(2000, 12, 12), 100);
        filmToUpdate.setId(filmService.getFilms().get(0).getId());
        filmService.updateFilm(filmToUpdate);
        assertEquals(filmToUpdate, filmService.getFilms().get(0));
        assertEquals(1, filmService.getFilms().size());
    }

    @Test
    void updateFilmWithoutId() {
        assertThrows(StorageException.class, () -> filmService.updateFilm(film));

        film.setId(5);
        assertThrows(StorageException.class, () -> filmService.updateFilm(film));
    }

    @Test
    void getFilmByIdTest() {
        assertThrows(StorageException.class, () -> filmService.getFilm(-1));
        assertThrows(StorageException.class, () -> filmService.getFilm(0));

        filmService.createFilm(film);
        assertEquals(film, filmService.getFilm(film.getId()));
    }

    @Test
    void getFilmsTest() {
        assertTrue(filmService.getFilms().isEmpty());

        filmService.createFilm(film);
        assertFalse(filmService.getFilms().isEmpty());

        List<Film> films = filmService.getFilms();
        assertSame(films.get(0).getClass(), Film.class);
    }

    @Test
    void spacesInNameFilmTest() {
        film.setName(" f ");
        assertEquals("f", filmService.createFilm(film).getName());
        film.setName(" f ");
        assertEquals("f", filmService.updateFilm(film).getName());
    }

    @Test
    void spaceIsLastInName() {
        film.setName("name ");
        filmService.createFilm(film);

        Film filmInController1 = filmService.getFilms().get(0);
        assertEquals("name", filmInController1.getName());

        film.setName("name ");
        filmService.updateFilm(film);
        Film filmInController2 = filmService.getFilms().get(0);
        assertEquals("name", filmInController2.getName());
    }

    @Test
    void spacesInDescriptionFilmTest() {
        film.setDescription(" f ");
        assertEquals("f", filmService.createFilm(film).getDescription());
        film.setDescription(" f ");
        assertEquals("f", filmService.updateFilm(film).getDescription());
    }

    @Test
    void spaceIsLastInDescription() {
        film.setDescription("description ");
        filmService.createFilm(film);

        Film filmInController1 = filmService.getFilms().get(0);
        assertEquals("description", filmInController1.getDescription());

        film.setName("description ");
        filmService.updateFilm(film);
        Film filmInController2 = filmService.getFilms().get(0);
        assertEquals("description", filmInController2.getDescription());
    }

    @Test
    void deleteFilmTest() {
        filmService.createFilm(film);
        assertEquals(1, filmService.getFilms().size());
        filmService.deleteFilm(film.getId());
        assertEquals(0, filmService.getFilms().size());

        assertThrows(StorageException.class, () -> filmService.deleteFilm(film.getId()));
    }

    @Test
    void correctAddAndDeleteLikeToFilm() {
        filmService.createFilm(film);
        assertEquals(0, filmService.getFilms().get(0).getLikesNumber());
        assertThrows(StorageException.class, () -> filmService.removeLikeFromFilm(film.getId(), user.getId()));

        filmService.addLikeToFilm(film.getId(), user.getId());
        assertEquals(1, filmService.getFilms().get(0).getLikesNumber());
        filmService.removeLikeFromFilm(film.getId(), user.getId());
        assertEquals(0, filmService.getFilms().get(0).getLikesNumber());
    }

    @Test
    void addAndDeleteLikeNonexistentFilm() {
        assertThrows(StorageException.class, () -> filmService.addLikeToFilm(-1, user.getId()));
        assertThrows(StorageException.class, () -> filmService.removeLikeFromFilm(-1, user.getId()));
    }

    @Test
    void addAndDeleteLikeNonexistentUser() {
        filmService.createFilm(film);
        assertThrows(StorageException.class, () -> filmService.addLikeToFilm(film.getId(), -1));
        assertThrows(StorageException.class, () -> filmService.removeLikeFromFilm(film.getId(), -1));
    }

    @Test
    void getTopFilmsTest() {
        assertTrue(filmService.getTopFilms(10).isEmpty());

        Film film1 = new Film("1film", "desc", LocalDate.of(2020, 12, 12), 100);
        Film film2 = new Film("2film", "desc", LocalDate.of(2020, 12, 12), 100);
        Film film3 = new Film("3film", "desc", LocalDate.of(2020, 12, 12), 100);

        filmService.createFilm(film);
        filmService.createFilm(film1);
        filmService.createFilm(film2);
        filmService.createFilm(film3);
        assertEquals(4, filmService.getTopFilms(10).size());

        film.addLike(1);
        film.addLike(2);
        film.addLike(3);
        film.addLike(4);

        film1.addLike(1);
        film1.addLike(2);
        film1.addLike(3);

        film2.addLike(1);
        film2.addLike(2);

        film3.addLike(1);

        filmService.updateFilm(film);
        filmService.updateFilm(film1);
        filmService.updateFilm(film2);
        filmService.updateFilm(film3);

        List<Film> topFilms = List.of(film, film1, film2, film3);
        assertEquals(topFilms, filmService.getTopFilms(10));

        film3.addLike(1);
        film3.addLike(2);
        film3.addLike(3);
        film3.addLike(4);
        film3.addLike(5);
        filmService.updateFilm(film3);

        List<Film> topFilms2 = List.of(film3, film, film1, film2);
        assertEquals(topFilms2, filmService.getTopFilms(10));

        int i = 0;

        while (i < 20) {
            filmService.createFilm(new Film(i + 4 + "film", "desc", LocalDate.of(2020, 12, 12), 100));
            i++;
        }

        assertEquals(24, filmService.getTopFilms(24).size());
        assertEquals(5, filmService.getTopFilms(5).size());
        assertEquals(0, filmService.getTopFilms(0).size());
    }
}