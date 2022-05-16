package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.util.ValidationException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private static int idCounter = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public String createUser(@Valid @RequestBody User user) {
        log.info("Получен для создания user: {}", user);
        checkAndSetId(user);
        checkLoginAndName(user);
        users.put(user.getId(), user);
        log.info("User: {} успешно создан", user);
        return "Учетная запись успешно создана";
    }

    @PutMapping
    public String updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.warn("Ошибка обновления учетной записи, пользователя не существует user: {}", user);
            throw new ValidationException("Ошибка обновления учетной записи, пользователя не существует");
        }
        log.info("Получен для обновления user {}", user);
        checkLoginAndName(user);
        users.put(user.getId(), user);
        log.info("User {} успешно обновлен", user);
        return "Учетная запись успешно обновлена";
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private void checkLoginAndName(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка формата логина");
            throw new ValidationException("Логин не может содержать пробельные символы");
        }
    }

    private void checkAndSetId(User user) {
        if (user.getId() == null) {
            user.setId(idCounter++);
        } else if (user.getId() >= 0) {
            log.warn("Ошибка создания пользователя, получен пользователь с изначально заданным id");
            throw new ValidationException("Ошибка создания пользователя, неверный формат id");
        }
    }
}