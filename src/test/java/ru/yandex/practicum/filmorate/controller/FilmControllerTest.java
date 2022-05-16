package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmControllerTest {
    private FilmController controller;
    private Film film;

    @BeforeEach
    void beforeEach() {
        controller = new FilmController();
        film = new Film("film", "description", LocalDate.of(2000, 12, 12), 100);
    }

    @Test
    void correctCreateFilmTest() {
        controller.createFilm(film);
        assertFalse(controller.getFilms().isEmpty());
    }

    @Test
    void createFilmWithId() {
        controller.createFilm(film);
        Film filmWithId = new Film("Incorrect", "Date", LocalDate.of(2000, 12, 27), 100);
        filmWithId.setId(controller.getFilms().get(0).getId());
        assertThrows(ValidationException.class, () -> controller.createFilm(filmWithId));
    }

    @Test
    void filmWithIncorrectDate() {
        controller.createFilm(film);
        Film filmWithPastDate = new Film("Incorrect", "Date", LocalDate.of(1895, 12, 27), 100);
        assertThrows(ValidationException.class, () -> controller.createFilm(filmWithPastDate));

        filmWithPastDate.setId(controller.getFilms().get(0).getId());
        assertThrows(ValidationException.class, () -> controller.updateFilm(filmWithPastDate));
    }

    @Test
    void correctUpdateTest() {
        controller.createFilm(film);
        Film filmToUpdate = new Film("Up", "Date", LocalDate.of(2000, 12, 12), 100);
        filmToUpdate.setId(controller.getFilms().get(0).getId());
        controller.updateFilm(filmToUpdate);
        assertEquals(filmToUpdate, controller.getFilms().get(0));
    }

    @Test
    void updateFilmWithoutId() {
        assertThrows(ValidationException.class, () -> controller.updateFilm(film));

        film.setId(5);
        assertThrows(ValidationException.class, () -> controller.updateFilm(film));
    }

    @Test
    void getFilmsTest() {
        assertTrue(controller.getFilms().isEmpty());

        controller.createFilm(film);
        assertFalse(controller.getFilms().isEmpty());

        List<Film> films = controller.getFilms();
        assertSame(films.get(0).getClass(), Film.class);
    }

    @Test
    void spacesInNameFilmTest() {
        film.setName(" f ");
        assertThrows(ValidationException.class, () -> controller.createFilm(film));
        assertThrows(ValidationException.class, () -> controller.updateFilm(film));
    }

    @Test
    void spaceIsLastInName() {
        film.setName("name ");
        controller.createFilm(film);

        Film filmInController1 = controller.getFilms().get(0);
        assertEquals("name", filmInController1.getName());

        film.setName("name ");
        controller.updateFilm(film);
        Film filmInController2 = controller.getFilms().get(0);
        assertEquals("name", filmInController2.getName());
    }

    @Test
    void spacesInDescriptionFilmTest() {
        film.setDescription(" f ");
        assertThrows(ValidationException.class, () -> controller.createFilm(film));
        assertThrows(ValidationException.class, () -> controller.updateFilm(film));
    }

    @Test
    void spaceIsLastInDescription() {
        film.setDescription("description ");
        controller.createFilm(film);

        Film filmInController1 = controller.getFilms().get(0);
        assertEquals("description", filmInController1.getDescription());

        film.setName("description ");
        controller.updateFilm(film);
        Film filmInController2 = controller.getFilms().get(0);
        assertEquals("description", filmInController2.getDescription());
    }
}