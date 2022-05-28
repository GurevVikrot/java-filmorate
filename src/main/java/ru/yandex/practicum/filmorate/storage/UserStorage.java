package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    boolean createUser(User user);

    boolean updateUser(User user);

    boolean deleteUser(Integer id);

    User getUser(Integer userId);

    List<User> getAllUsers();

    boolean userExist(Integer id);
}
