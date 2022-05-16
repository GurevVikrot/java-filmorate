package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.util.ValidationException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static int idCounter = 0;
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public String createFilm(@Valid @RequestBody Film film) {
        log.info("Получен для создания film: {}", film);
        checkAndSetId(film);
        checkSpace(film);
        checkDateFilm(film);
        films.put(film.getId(), film);
        log.info("film: {} успешно создан", film);
        return "Фильм успешно добавлен";
    }

    @PutMapping
    public String updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен для обновления film: {}", film);

        if (film.getId() == null) {
            log.warn("Фильма нет в коллекции или он не создан, невозможно обновить");
            throw new ValidationException("Фильма нет в коллекции или он не создан, невозможно обновить");
        }
        checkSpace(film);
        checkDateFilm(film);
        films.put(film.getId(), film);
        log.info("film: {} успешно обновлен", film);
        return "Фильм успешно обновлен";
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private void checkDateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("Время выпуска фильма меньше минимальной даты: 28-12-1895 film: {}", film);
            throw new ValidationException("Время выпуска фильма меньше минимальной даты: 28-12-1895");
        }
    }

    private void checkAndSetId(Film film) {
        if (film.getId() == null) {
            film.setId(idCounter++);
        } else if (film.getId() >= 0) {
            log.warn("Ошибка создания фильма, получен фильм с изначально заданным id");
            throw new ValidationException("Ошибка создания фильма, неверный формат id");
        }
    }

    private void checkSpace(Film film) {
        if (film.getName().startsWith(" ")) {
            log.warn("Ошибка создания фильма, название начинается с ' '");
            throw new ValidationException("Ошибка создания фильма, название не может начинаться с пробела");
        } else if (film.getDescription().startsWith(" ")) {
            log.warn("Ошибка создания фильма, описание начинается с ' '");
            throw new ValidationException("Ошибка создания фильма, описание не может начинаться с пробела");
        } else {
            film.setName(film.getName().trim());
            film.setDescription(film.getDescription().trim());
        }
    }
}