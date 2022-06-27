package ru.yandex.practicum.filmorate.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class DefaultAdvice {

    @ExceptionHandler({ValidationException.class, NumberFormatException.class})
    public ResponseEntity<String> validationException(ValidationException exp) {
        return new ResponseEntity<>(exp.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<String> storageException(StorageException exp) {
        return new ResponseEntity<>(exp.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<?> exc(ConstraintViolationException ex){
        return new ResponseEntity<>("Ошибка валидации, проверьте передаваемые значения. " +
                "Они должны быть существующими и быть больше 0"
                , HttpStatus.NOT_FOUND);
    }
}