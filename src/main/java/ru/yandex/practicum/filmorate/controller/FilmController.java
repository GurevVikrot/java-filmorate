package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Получен для создания film: {}", film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен для обновления film: {}", film);
        return filmService.updateFilm(film);
    }

    @DeleteMapping
    public String deleteFilm(@RequestParam @NotNull @Positive Long id) {
        return filmService.deleteFilm(id);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос на получение всех фильмов");
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable @NotNull @Positive Long id) {
        log.info("Получен запрос на получение фильма по id = {}", id);
        return filmService.getFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(required = false, defaultValue = "10") @Positive String count) {
        log.info("Получен запрос на получение топ {} фильмов", count);
        return filmService.getTopFilms(Integer.parseInt(count));
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean addLikeToFilm(@PathVariable @NotNull @Positive Long id,
                                 @PathVariable @NotNull @Positive Long userId) {
        log.info("Получен запрос на добавления like фильму id = {} от пользователя id {}", id, userId);
        return filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLikeFromFilm(@PathVariable @NotNull @Positive Long id,
                                      @PathVariable @NotNull @Positive Long userId) {
        log.info("Получен запрос на удаления like фильма id = {} от пользователя id {}", id, userId);
        return filmService.removeLikeFromFilm(id, userId);
    }
}