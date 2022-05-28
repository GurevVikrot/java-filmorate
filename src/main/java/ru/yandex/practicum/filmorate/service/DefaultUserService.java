package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.StorageException;
import ru.yandex.practicum.filmorate.util.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DefaultUserService implements UserService {
    private static int idCounter = 1;
    private final UserStorage userStorage;

    @Autowired
    public DefaultUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User createUser(User user) {
        checkAndSetId(user);
        checkLoginAndName(user);

        if (userStorage.createUser(user)) {
            log.info("Создан пользователь {}", user);
            return user;
        }

        log.warn("Ошибка создания пользователя {}", user);
        throw new StorageException("Ошибка создания пользователя, возможно пользователь уже существует");
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            log.warn("Ошибка обновления учетной записи, передан пользователь с id = null");
            throw new ValidationException("Ошибка обновления учетной записи, пользователя не существует");
        }

        checkLoginAndName(user);

        if (userStorage.updateUser(user)) {
            log.info("Пользователь обновлен {}", user);
            return userStorage.getUser(user.getId());
        }

        log.warn("Ошибка обновления учетной записи, передан пользователь с несуществующим id = {}", user.getId());
        throw new StorageException("Ошибка обновления учетной записи, пользователя не существует");
    }

    @Override
    public String deleteUser(Integer id) {
        if (userStorage.deleteUser(id)) {
            log.info("Пользователь с id = {} удален", id);
            return "Пользователь удален";
        }

        log.warn("Ошибка удаления учетной записи, передан пользователь с несуществующим id = {}", id);
        throw new StorageException("Удаление не возможно, пользователя не существует");
    }

    @Override
    public List<User> getUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        if (userStorage.userExist(id)) {
            User user = userStorage.getUser(id);
            List<User> friends = new ArrayList<>();

            for (int friendId : user.getFriendsId()) {
                friends.add(userStorage.getUser(friendId));
            }

            return friends;
        } else {
            throw new StorageException("Пользователя не существует, невозможно отобразить друзей");
        }
    }

    @Override
    public List<User> getMutualFriends(Integer userId, Integer friendId) {
        if (userStorage.userExist(userId) && userStorage.userExist(friendId)) {
            User user = userStorage.getUser(userId);
            List<Integer> userFriends = user.getFriendsId();

            User friend = userStorage.getUser(friendId);
            List<Integer> friendFriends = friend.getFriendsId();

            return userFriends.stream()
                    .filter(friendFriends::contains)
                    .map(userStorage::getUser)
                    .collect(Collectors.toList());
        } else {
            throw new StorageException("Пользователя не существует, невозможно отобразить друзей");
        }
    }

    @Override
    public User addToFriends(Integer userId, Integer friendId) {
        if (userStorage.userExist(userId) && userStorage.userExist(friendId) && !Objects.equals(userId, friendId)) {
            if (userStorage.getUser(userId).addFriend(friendId) &&
                    userStorage.getUser(friendId).addFriend(userId)) {
                log.info("Пользователи id = {} и id = {} взаимно добавлены в друзья", userId, friendId);
                return userStorage.getUser(friendId);
            } else {
                log.info("Неудачное добавление в друзья, пользователи " +
                        "id = {} и id = {} уже ими являются", userId, friendId);
                throw new StorageException("Невозможно добавить пользователя в друзья, когда вы уже ими являетесь");
            }
        }

        log.warn("Ошибка операции пользователя id = {} и/или id = {} не существует", userId, friendId);
        throw new StorageException("Невозможно выполнить действие, одного из пользователей не существует");
    }

    @Override
    public String removeFromFriends(Integer userId, Integer friendId) {
        if (userStorage.userExist(userId) && userStorage.userExist(friendId) && !Objects.equals(userId, friendId)) {
            if (userStorage.getUser(userId).removeFriend(friendId) &&
                    userStorage.getUser(friendId).removeFriend(userId)) {
                log.info("Пользователи id = {} и id = {} взаимно удалены из друзей", userId, friendId);
                return "Пользователь удален из друзей";
            } else {
                log.info("Неудачное удаление из друзей, пользователи " +
                        "id = {} и id = {} ими не являются", userId, friendId);
                throw new StorageException("Невозможно удалить пользователя из друзей, ими не являетесь");
            }
        }

        log.warn("Ошибка операции пользователя id = {} и/или id = {} не существует", userId, friendId);
        throw new StorageException("Невозможно выполнить действие, одного из пользователей не существует");
    }

    @Override
    public User getUser(Integer id) {
        User user = userStorage.getUser(id);

        if (user == null) {
            log.warn("Ошибка получения пользователя, передан пользователь с несуществующим id = {}", id);
            throw new StorageException("Пользователя не существует");
        }

        return user;
    }

    private void checkLoginAndName(User user) {
        String userName = user.getName();
        if (userName == null || userName.isEmpty()) {
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