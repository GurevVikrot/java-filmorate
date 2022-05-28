package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен для создания user: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен для обновления user {}", user);
        return userService.updateUser(user);
    }

    @DeleteMapping
    public String deleteUser(@NotNull @RequestParam Integer id) {
        log.info("Получен для удаления user с id {}", id);
        return userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public User getUser(@NotNull @PathVariable Integer id) {
        log.info("Получен запрос на получение пользователя id = {}", id);
        return userService.getUser(id);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен запрос на получение списка пользователей");
        return userService.getUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addUserToFriend(@NotNull @PathVariable Integer id,
                                  @NotNull @PathVariable Integer friendId) {
        log.info("Запрос добавления друга. Пользователь id = {}, друг id = {}", id, friendId);
        return userService.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public String removeUserFromFriend(@NotNull @PathVariable Integer id,
                                       @NotNull @PathVariable Integer friendId) {
        log.info("Запрос удаления друга. Пользователь id = {}, друг id = {}", id, friendId);
        return userService.removeFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@NotNull @PathVariable Integer id) {
        log.info("Запрос получения друзей пользователя id = {}", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getUserFriends(@NotNull @PathVariable Integer id,
                                        @NotNull @PathVariable Integer otherId) {
        log.info("Запрос получения общих друзей пользователя id = {} и пользователя id = {}", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }
}