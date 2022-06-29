package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.film.GenreDAO;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.util.StorageException;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreDAO genreDAO;

    @Autowired
    public GenreController(GenreDAO genreDAO) {
        this.genreDAO = genreDAO;
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("Получен запрос на получения списка жанров");
        return genreDAO.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable @NotNull @Positive int id) {
        log.info("Получен запрос на получения жанра id = {}", id);
        return genreDAO.getGenre(id).orElseThrow(() -> new StorageException("Жанр не найден",
                "Ошибка получения жанра, жанр не найден"));
    }
}