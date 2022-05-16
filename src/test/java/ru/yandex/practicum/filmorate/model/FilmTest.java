package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmTest {
    private static Validator validator;
    private static ValidatorFactory factory;
    private Film film;

    @BeforeAll
    public static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    public static void afterAll() {
        factory.close();
    }

    @BeforeEach
    void beforeEach() {
        film = new Film("film", "description", LocalDate.of(2000, 12, 12), 100);
        validator = factory.getValidator();
    }

    @Test
    void validDurationFilmTest() {
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void invalidDurationFilmTest() {
        Film filmToTest = new Film("Up", "Date", LocalDate.of(2000, 12, 12), -1);
        assertFalse(validator.validate(filmToTest).isEmpty());
    }

    @Test
    void nullDurationFilmTest() {
        Film filmToTest = new Film("Up", "Date", LocalDate.of(2000, 12, 12), null);
        assertFalse(validator.validate(filmToTest).isEmpty());
    }

    @Test
    void validNameFilmTest() {
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void invalidNameFilmTest() {
        film.setName("");
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void invalidNameFilmTest2() {
        film.setName(" ");
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void nullNameTest() {
        film.setName(null);
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void validDescriptionFilmTest() {
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void invalidDescriptionFilmTest() {
        film.setDescription("");
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void invalidDescriptionFilmTest2() {
        film.setDescription(" ");
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void nullDescriptionTest() {
        film.setDescription(null);
        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void sizeOfDescriptionTest() {
        film.setDescription("0123456789".repeat(20));
        assertTrue(validator.validate(film).isEmpty());

        film.setDescription(film.getDescription() + "a");
        assertFalse(validator.validate(film).isEmpty());
    }
}