package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryFilmStorageTest {
    private InMemoryFilmStorage filmStorage;
    private Film film;

    @BeforeEach
    void BeforeEach() {
        filmStorage = new InMemoryFilmStorage();
        film = new Film("film", "desc", LocalDate.of(2020, 12, 12), 100);
        film.setId(0);
    }

    @Test
    void correctSaveTest() {
        assertTrue(filmStorage.saveFilm(film));
        assertEquals(film, filmStorage.getFilm(film.getId()));
    }

    @Test
    void saveExistFilm() {
        assertTrue(filmStorage.saveFilm(film));
        assertFalse(filmStorage.saveFilm(film));
        assertEquals(1, filmStorage.getFilms().size());
    }

    @Test
    void correctUpdateFilm() {
        filmStorage.saveFilm(film);
        assertEquals(1, filmStorage.getFilms().size());

        Film updatedFilm = new Film("Up", "date", LocalDate.of(2020, 12, 12), 100);
        updatedFilm.setId(0);

        assertTrue(filmStorage.updateFilm(updatedFilm));
        assertEquals(updatedFilm, filmStorage.getFilm(updatedFilm.getId()));
        assertEquals(1, filmStorage.getFilms().size());
    }

    @Test
    void updateNonexistFilm() {
        assertFalse(filmStorage.updateFilm(film));
        assertEquals(0, filmStorage.getFilms().size());
    }

    @Test
    void getFilmTest() {
        filmStorage.saveFilm(film);
        assertEquals(film, filmStorage.getFilm(film.getId()));
        assertNull(filmStorage.getFilm(-1));
    }

    @Test
    void deleteFilmTest() {
        filmStorage.saveFilm(film);
        assertEquals(film, filmStorage.getFilm(film.getId()));

        filmStorage.deleteFilm(film.getId());
        assertNull(filmStorage.getFilm(film.getId()));
        assertEquals(0, filmStorage.getFilms().size());

        assertFalse(filmStorage.deleteFilm(0));
    }

    @Test
    void getFilmsTest() {
        assertEquals(0, filmStorage.getFilms().size());

        Film film1 = new Film("1film", "desc", LocalDate.of(2020, 12, 12), 100);
        film1.setId(1);
        Film film2 = new Film("2film", "desc", LocalDate.of(2020, 12, 12), 100);
        film2.setId(2);
        Film film3 = new Film("3film", "desc", LocalDate.of(2020, 12, 12), 100);
        film3.setId(3);

        filmStorage.saveFilm(film);
        filmStorage.saveFilm(film1);
        filmStorage.saveFilm(film2);
        filmStorage.saveFilm(film3);

        assertEquals(4, filmStorage.getFilms().size());
    }

    @Test
    void topLikeFilmsTest() {
        assertEquals(0, filmStorage.getTopFilms(10).size());

        Film film1 = new Film("1film", "desc", LocalDate.of(2020, 12, 12), 100);
        film1.setId(1);
        Film film2 = new Film("2film", "desc", LocalDate.of(2020, 12, 12), 100);
        film2.setId(2);
        Film film3 = new Film("3film", "desc", LocalDate.of(2020, 12, 12), 100);
        film3.setId(3);

        filmStorage.saveFilm(film);
        filmStorage.saveFilm(film1);
        filmStorage.saveFilm(film2);
        filmStorage.saveFilm(film3);
        assertEquals(4, filmStorage.getTopFilms(10).size());

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

        filmStorage.updateFilm(film);
        filmStorage.updateFilm(film1);
        filmStorage.updateFilm(film2);
        filmStorage.updateFilm(film3);

        List<Film> topFilms = List.of(film, film1, film2, film3);
        assertEquals(topFilms, filmStorage.getTopFilms(10));

        film3.addLike(1);
        film3.addLike(2);
        film3.addLike(3);
        film3.addLike(4);
        film3.addLike(5);
        filmStorage.updateFilm(film3);

        List<Film> topFilms2 = List.of(film3, film, film1, film2);
        assertEquals(topFilms2, filmStorage.getTopFilms(10));
    }
}