package ru.yandex.practicum.filmorate.util;

public class ValidationException extends IllegalArgumentException {
    public ValidationException (String massage){
        super(massage);
    }
}