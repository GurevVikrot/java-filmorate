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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DefaultUserService implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public DefaultUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User createUser(User user) {
        checkNonexistentId(user);
        checkLoginAndName(user);
        Optional<User> userOptional = userStorage.createUser(user);

        if (userOptional.isPresent()) {
            log.info("Создан пользователь {}", user);
            return userOptional.get();
        }

        log.warn("Ошибка создания пользователя {}", user);
        throw new StorageException("Ошибка создания пользователя, возможно пользователь уже существует");
    }

    @Override
    public User updateUser(User user) {
        checkLoginAndName(user);

        if (userStorage.updateUser(user)) {
            log.info("Пользователь обновлен {}", user);
            return getFromStorage(user.getId());
        }

        log.warn("Ошибка обновления учетной записи, передан пользователь с несуществующим id = {}", user.getId());
        throw new StorageException("Ошибка обновления учетной записи, пользователя не существует");
    }

    @Override
    public String deleteUser(long id) {
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
    public List<User> getUserFriends(long id) {
        User user = getFromStorage(id);
        List<User> friends = new ArrayList<>();

        for (long friendId : user.getFriendsId()) {
            userStorage.getUser(friendId).ifPresent(friends::add);
        }

        return friends;
    }

    @Override
    public List<User> getMutualFriends(long userId, long friendId) {
        User user = getFromStorage(userId);
        List<Long> userFriends = user.getFriendsId();

        User friend = getFromStorage(friendId);
        List<Long> friendFriends = friend.getFriendsId();

        return userFriends.stream()
                .filter(friendFriends::contains)
                .map(this::getFromStorage)
                .collect(Collectors.toList());
    }

    @Override
    public User addToFriends(long userId, long friendId) {
        if (!Objects.equals(userId, friendId)) {
            if (getFromStorage(userId).addFriend(friendId) &&
                    getFromStorage(friendId).addFriend(userId)) {
                log.info("Пользователи id = {} и id = {} взаимно добавлены в друзья", userId, friendId);
                return getFromStorage(friendId);
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
    public String removeFromFriends(long userId, long friendId) {
        if (!Objects.equals(userId, friendId)) {
            if (getFromStorage(userId).removeFriend(friendId) &&
                    getFromStorage(friendId).removeFriend(userId)) {
                log.info("Пользователи id = {} и id = {} взаимно удалены из друзей", userId, friendId);
                return "Пользователь удален из друзей";
            } else {
                log.info("Неудачное удаление из друзей, пользователи " +
                        "id = {} и id = {} ими не являются", userId, friendId);
                throw new StorageException("Невозможно удалить пользователя из друзей, вы ими не являетесь");
            }
        }

        log.warn("Ошибка операции пользователя id = {} и/или id = {} не существует", userId, friendId);
        throw new StorageException("Невозможно выполнить действие, одного из пользователей не существует");
    }

    @Override
    public User getUser(long id) {
        return getFromStorage(id);
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

    private void checkNonexistentId(User user) {
        if (user.getId() > 0) {
            log.warn("Ошибка создания пользователя, получен пользователь с изначально заданным id");
            throw new ValidationException("Ошибка создания пользователя, неверный формат id");
        }
    }

    private User getFromStorage(long id) {
        return userStorage.getUser(id).orElseThrow(
                () -> new StorageException("Пользователя не существует",
                        "Ошибка получения пользователя id = {} из хранилища, возвращается null"));
    }
}