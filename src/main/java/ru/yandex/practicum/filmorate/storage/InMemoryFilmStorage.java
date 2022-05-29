package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static int idCounter = 1;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Optional<Film> saveFilm(Film film) {
        if (film.getId() == 0) {
            film.setId(idCounter++);
            films.put(film.getId(), film);
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Film> getFilm(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public boolean deleteFilm(long id) {
        if (films.containsKey(id)) {
            films.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return films.values().stream()
                .sorted((film1, film2) -> {
                    if (film1.getLikesNumber() > film2.getLikesNumber()) {
                        return -1;
                    }
                    return 1;
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}