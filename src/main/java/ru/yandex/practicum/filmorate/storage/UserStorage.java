package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> createUser(User user);

    boolean updateUser(User user);

    boolean deleteUser(long id);

    Optional<User> getUser(long userId);

    List<User> getAllUsers();

    boolean userExist(long id);
}
