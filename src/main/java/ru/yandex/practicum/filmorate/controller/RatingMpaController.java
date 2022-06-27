package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.film.RatingMpaDAO;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.util.StorageException;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequestMapping("/mpa")
@Slf4j
public class RatingMpaController {
    RatingMpaDAO ratingMpaDAO;

    @Autowired
    public RatingMpaController(RatingMpaDAO ratingMpaDAO) {
        this.ratingMpaDAO = ratingMpaDAO;
    }

    @GetMapping
    public List<RatingMpa> getAllRatings() {
        log.info("Получен запрос на получение всех рейтингов МРА");
        return ratingMpaDAO.getAllRatings();
    }

    @GetMapping("/{id}")
    public RatingMpa getRatingById(@PathVariable @NotNull @Positive int id) {
        log.info("Получен запрос на получение всех рейтинга id = {}", id);
        return ratingMpaDAO.getRatingById(id).orElseThrow(
                () -> new StorageException("Рейтинг сломался", "При получении рейтинга произошла ошибка"));
    }
}