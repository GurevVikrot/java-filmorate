package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    String deleteUser(long id);

    List<User> getUsers();

    List<User> getUserFriends(long id);

    List<User> getMutualFriends(long userId, long friendId);

    User addToFriends(long userId, long friendId);

    String removeFromFriends(long userId, long friendId);

    User getUser(long id);

    User confirmFriendship(Long id, Long otherId);
}
