package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RatingMpaDAOTest {
    private final RatingMpaDAO ratingMpaDAO;
    private final List<RatingMpa> expectedRatings = List.of(
            new RatingMpa(1, "G"),
            new RatingMpa(2, "PG"),
            new RatingMpa(3, "PG-13"),
            new RatingMpa(4, "R"),
            new RatingMpa(5, "NC-17")
    );

    @Test
    public void getAllRatingsTest() {
        List<RatingMpa> ratings = ratingMpaDAO.getAllRatings();
        assertNotNull(ratings);
        assertEquals(expectedRatings, ratings);
    }

    @Test
    public void getRatingByIdTest() {
        Optional<RatingMpa> rating0 = ratingMpaDAO.getRatingById(0);
        assertThat(rating0)
                .isEmpty();

        Optional<RatingMpa> rating1 = ratingMpaDAO.getRatingById(1);
        assertThat(rating1)
                .isPresent()
                .hasValueSatisfying(genre -> assertThat(genre).hasFieldOrPropertyWithValue("id", 1))
                .hasValueSatisfying(genre -> assertThat(genre).hasFieldOrPropertyWithValue("name", "G"));

    }
}