package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GenreDAOTest {
    private final GenreDAO genreDAO;

    @Test
    void getAllGenres() {
        assertNotNull(genreDAO.getAllGenres());
        assertFalse(genreDAO.getAllGenres().isEmpty());
    }

    @Test
    void getGenre() {
        Optional<Genre> genre0 = genreDAO.getGenre(0);
        assertThat(genre0)
                .isEmpty();

        Optional<Genre> genre1 = genreDAO.getGenre(1);
        assertThat(genre1)
                .isPresent()
                .hasValueSatisfying(genre -> assertThat(genre).hasFieldOrPropertyWithValue("id", 1));
    }
}