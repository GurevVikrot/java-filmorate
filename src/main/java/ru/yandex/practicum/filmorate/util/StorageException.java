package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StorageException extends RuntimeException {
    public StorageException(String massage) {
        super(massage);
    }

    public StorageException(String massage, String cause) {
        super(massage);
        log.error(cause);
    }
}