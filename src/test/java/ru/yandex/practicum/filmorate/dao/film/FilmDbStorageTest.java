package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.StorageException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final DbFilmLikesDAO likesDAO;
    private final UserDbStorage userDbStorage;
    private Film film;

    @BeforeEach
    void BeforeEach() {
        film = new Film("film", "desc", LocalDate.of(2020, 12, 12), 100);
        film.setMpa(new RatingMpa(1, "G"));
    }

    @Test
    public void saveFilm() {
        Optional<Film> filmOptional = filmDbStorage.saveFilm(film);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
        film.setId(1);
        assertEquals(film, filmOptional.get());

        film.setGenres(Set.of(new Genre(1, "Комедия")));
        Optional<Film> filmWithGenreOptional = filmDbStorage.saveFilm(film);
        assertThat(filmWithGenreOptional)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("id", 2L));
        film.setId(2);
        assertEquals(film, filmWithGenreOptional.get());
        assertEquals(1, filmWithGenreOptional.get().getGenres().size());
    }

    @Test
    public void saveFilmWithId() {
        film.setId(-1);
        Optional<Film> filmOptional = filmDbStorage.saveFilm(film);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
        film.setId(1);
        assertEquals(film, filmOptional.get());
    }

    @Test
    public void updateFilm() {
        filmDbStorage.saveFilm(film);
        film.setName("Update");
        film.setId(1);

        Optional<Film> filmOptional = filmDbStorage.updateFilm(film);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
        assertEquals(film, filmOptional.get());

        film.setGenres(Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")));

        Optional<Film> filmGenreOptional = filmDbStorage.updateFilm(film);
        assertThat(filmGenreOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
        assertEquals(film, filmGenreOptional.get());
    }

    @Test
    public void updateNonexistentFilm() {
        Optional<Film> filmOptional = filmDbStorage.updateFilm(film);
        assertThat(filmOptional)
                .isEmpty();
    }

    @Test
    public void saveAndUpdateNullFilm() {
        assertThrows(StorageException.class, () -> filmDbStorage.saveFilm(null));
        assertThrows(StorageException.class, () -> filmDbStorage.updateFilm(null));
    }

    @Test
    public void getAllFilms() {
        assertTrue(filmDbStorage.getFilms().isEmpty());

        filmDbStorage.saveFilm(film);
        filmDbStorage.saveFilm(film);
        filmDbStorage.saveFilm(film);

        List<Film> films = filmDbStorage.getFilms();
        assertFalse(films.isEmpty());
        assertEquals(3, films.size());

        for (int i = 1; i <= films.size(); i++) {
            film.setId(i);
            assertEquals(film, films.get(i - 1));
        }
    }

    @Test
    public void deleteTest() {
        assertFalse(filmDbStorage.deleteFilm(-1));
        Optional<Film> filmOptional = filmDbStorage.getFilm(1);
        assertThat(filmOptional)
                .isEmpty();

        filmDbStorage.saveFilm(film);
        assertTrue(filmDbStorage.deleteFilm(1));
        Optional<Film> filmOptional1 = filmDbStorage.getFilm(1);
        assertThat(filmOptional1)
                .isEmpty();
    }

    @Test
    public void getFilmTest() {
        Optional<Film> filmOptional = filmDbStorage.getFilm(1);
        assertThat(filmOptional)
                .isEmpty();

        Optional<Film> filmOptional1 = filmDbStorage.saveFilm(film);
        assertThat(filmOptional1)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));

    }

    @Test
    public void getTopFilms() {
        List<Film> topFilms1 = filmDbStorage.getTopFilms(10);
        assertNotNull(topFilms1);
        assertTrue(topFilms1.isEmpty());

        filmDbStorage.saveFilm(film);
        filmDbStorage.saveFilm(film);
        filmDbStorage.saveFilm(film);

        List<Film> topFilms2 = filmDbStorage.getTopFilms(10);
        assertNotNull(topFilms2);
        assertFalse(topFilms2.isEmpty());
        assertEquals(3, topFilms2.size());

        userDbStorage.createUser(new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10)));
        userDbStorage.createUser(new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10)));

//        List<Film> films = filmDbStorage.getFilms();
//        List<User> users = userDbStorage.getAllUsers();

        likesDAO.addLike(1, 1);
        likesDAO.addLike(1, 2);
        likesDAO.addLike(2, 1);

        film.setId(1);
        film.addLike(1);
        film.addLike(2);
        Film film2 = new Film("film", "desc", LocalDate.of(2020, 12, 12), 100);
        film2.setId(2);
        film2.setMpa(new RatingMpa(1, "G"));
        film2.addLike(1);
        Film film3 = new Film("film", "desc", LocalDate.of(2020, 12, 12), 100);
        film3.setMpa(new RatingMpa(1, "G"));
        film3.setId(3);

        List<Film> filmsExpected = List.of(film, film2);
        assertEquals(filmsExpected, filmDbStorage.getTopFilms(10));
    }
}