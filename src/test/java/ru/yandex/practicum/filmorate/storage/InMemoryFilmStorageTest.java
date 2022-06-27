package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertTrue(filmStorage.saveFilm(film).isPresent());
        assertEquals(film, filmStorage.getFilm(film.getId()).orElse(null));
    }

    @Test
    void saveExistFilm() {
        assertTrue(filmStorage.saveFilm(film).isPresent());
        assertFalse(filmStorage.saveFilm(film).isPresent());
        assertEquals(1, filmStorage.getFilms().size());
    }

    @Test
    void correctUpdateFilm() {
        filmStorage.saveFilm(film);
        assertEquals(1, filmStorage.getFilms().size());

        Film updatedFilm = new Film("Up", "date", LocalDate.of(2020, 12, 12), 100);
        updatedFilm.setId(1);

        assertTrue(filmStorage.updateFilm(updatedFilm).isPresent());
        assertEquals(updatedFilm, filmStorage.getFilm(updatedFilm.getId()).orElse(null));
        assertEquals(1, filmStorage.getFilms().size());
    }

    @Test
    void updateNonexistentFilm() {
        assertFalse(filmStorage.updateFilm(film).isPresent());
        assertEquals(0, filmStorage.getFilms().size());
    }

    @Test
    void getFilmTest() {
        filmStorage.saveFilm(film);
        assertEquals(film, filmStorage.getFilm(film.getId()).orElse(null));
        assertFalse(filmStorage.getFilm(-1).isPresent());
    }

    @Test
    void deleteFilmTest() {
        filmStorage.saveFilm(film);
        assertEquals(film, filmStorage.getFilm(film.getId()).orElse(null));

        filmStorage.deleteFilm(film.getId());
        assertFalse(filmStorage.getFilm(film.getId()).isPresent());
        assertEquals(0, filmStorage.getFilms().size());

        assertFalse(filmStorage.deleteFilm(0));
    }

    @Test
    void getFilmsTest() {
        assertEquals(0, filmStorage.getFilms().size());

        Film film1 = new Film("1film", "desc", LocalDate.of(2020, 12, 12), 100);
        Film film2 = new Film("2film", "desc", LocalDate.of(2020, 12, 12), 100);
        Film film3 = new Film("3film", "desc", LocalDate.of(2020, 12, 12), 100);

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
        Film film2 = new Film("2film", "desc", LocalDate.of(2020, 12, 12), 100);
        Film film3 = new Film("3film", "desc", LocalDate.of(2020, 12, 12), 100);
        Film film4 = new Film("3film", "desc", LocalDate.of(2020, 12, 12), 100);

        filmStorage.saveFilm(film);
        filmStorage.saveFilm(film1);
        filmStorage.saveFilm(film2);
        filmStorage.saveFilm(film3);
        filmStorage.saveFilm(film4);
        assertEquals(5, filmStorage.getTopFilms(10).size());

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
        filmStorage.updateFilm(film4);

        List<Film> topFilms = List.of(film, film1, film2, film3, film4);
        assertEquals(topFilms, filmStorage.getTopFilms(10));

        film3.addLike(2);
        film3.addLike(3);
        film3.addLike(4);
        film3.addLike(5);
        film3.addLike(6);
        filmStorage.updateFilm(film3);

        List<Film> topFilms2 = List.of(film3, film, film1, film2, film4);
        assertEquals(topFilms2, filmStorage.getTopFilms(10));
    }
}