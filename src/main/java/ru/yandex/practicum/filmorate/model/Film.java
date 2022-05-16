package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NotNull
public class Film {
    private final LocalDate releaseDate;

    @NotNull
    @Positive(message = "Длительность фильма должна быть положительной")
    private final Integer duration;

    private Integer id;

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
}