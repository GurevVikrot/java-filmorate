package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NotNull
public class Film {
    private final LocalDate releaseDate;
    private final Set<Long> likesByUsers = new HashSet<>();
    private Set<Genre> genres;
    private RatingMpa mpa;

    @NotNull
    @Positive(message = "Длительность фильма должна быть положительной")
    private final Integer duration;

    private long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    @NotEmpty(message = "Название фильма не может быть пустым")
    private String name;

    @NotBlank(message = "Описание фильма не может быть пустым")
    @Size(max = 200, message = "Превышен максимум символов в описании - 200")
    private String description;

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public boolean addLike(long userId) {
        return likesByUsers.add(userId);
    }

    public int getLikesNumber() {
        return likesByUsers.size();
    }

    public boolean deleteLike(long userId) {
        return likesByUsers.remove(userId);
    }
}