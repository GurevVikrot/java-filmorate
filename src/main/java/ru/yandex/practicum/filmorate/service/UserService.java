package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    String deleteUser(Integer id);

    List<User> getUsers();

    List<User> getUserFriends(Integer id);

    List<User> getMutualFriends(Integer userId, Integer friendId);

    User addToFriends(Integer userId, Integer friendId);

    String removeFromFriends(Integer userId, Integer friendId);

    User getUser(Integer id);
}
