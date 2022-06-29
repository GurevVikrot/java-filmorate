package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbFilmLikesDAOTest {
    private final DbFilmLikesDAO dbFilmLikesDAO;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private Film film;
    private User user;

    @BeforeEach
    void BeforeEach() {
        film = new Film("film", "desc", LocalDate.of(2020, 12, 12), 100);
        film.setMpa(new RatingMpa(1, "G"));
        user = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
    }

    @Test
    void addLikeTest() {
        assertThrows(DataIntegrityViolationException.class, () -> dbFilmLikesDAO.addLike(1, 1));

        filmDbStorage.saveFilm(film);
        userDbStorage.createUser(user);

        assertThrows(DataIntegrityViolationException.class, () -> dbFilmLikesDAO.addLike(1, 2));
        assertThrows(DataIntegrityViolationException.class, () -> dbFilmLikesDAO.addLike(2, 1));
        assertTrue(dbFilmLikesDAO.addLike(1, 1));
        assertFalse(dbFilmLikesDAO.getFilmLikes(1).isEmpty());
        assertEquals(1, dbFilmLikesDAO.getFilmLikes(1).get(0));
        assertThrows(DataIntegrityViolationException.class, () -> dbFilmLikesDAO.addLike(1, 1));
    }

    @Test
    void removeLike() {
        assertFalse(dbFilmLikesDAO.removeLike(1, 1));

        filmDbStorage.saveFilm(film);
        userDbStorage.createUser(user);

        assertTrue(dbFilmLikesDAO.addLike(1, 1));
        assertTrue(dbFilmLikesDAO.removeLike(1, 1));
        assertTrue(dbFilmLikesDAO.getFilmLikes(1).isEmpty());
    }

    @Test
    void getFilmLikesAndGetTopFilmsIdTest() {
        filmDbStorage.saveFilm(film);
        userDbStorage.createUser(user);
        userDbStorage.createUser(user);
        userDbStorage.createUser(user);
        userDbStorage.createUser(user);

        assertTrue(dbFilmLikesDAO.getFilmLikes(1).isEmpty());
        assertTrue(dbFilmLikesDAO.getTopFilms(10).isEmpty());

        dbFilmLikesDAO.addLike(1, 1);
        dbFilmLikesDAO.addLike(1, 2);
        dbFilmLikesDAO.addLike(1, 3);
        dbFilmLikesDAO.addLike(1, 4);
        assertEquals(List.of(1L, 2L, 3L, 4L), dbFilmLikesDAO.getFilmLikes(1));

        filmDbStorage.saveFilm(film);
        filmDbStorage.saveFilm(film);
        filmDbStorage.saveFilm(film);

        dbFilmLikesDAO.addLike(4, 1);
        dbFilmLikesDAO.addLike(4, 2);
        dbFilmLikesDAO.addLike(4, 3);
        dbFilmLikesDAO.addLike(3, 1);
        dbFilmLikesDAO.addLike(3, 2);
        dbFilmLikesDAO.addLike(2, 4);
        dbFilmLikesDAO.addLike(2, 3);

        assertEquals(List.of(1L, 4L, 2L, 3L), dbFilmLikesDAO.getTopFilms(10));
        assertEquals(List.of(1L, 4L), dbFilmLikesDAO.getTopFilms(2));
    }
}