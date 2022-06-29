package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class RatingMpa {
    private int id;
    private String name;

    public RatingMpa(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
