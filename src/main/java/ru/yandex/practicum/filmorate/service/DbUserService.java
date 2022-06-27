package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.user.FriendshipDAO;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.StorageException;
import ru.yandex.practicum.filmorate.util.ValidationException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
public class DbUserService extends DefaultUserService {
    private final FriendshipDAO friendshipStorage;
    private final UserStorage storage;

    public DbUserService(UserStorage userStorage, FriendshipDAO friendshipStorage) {
        super(userStorage);
        this.friendshipStorage = friendshipStorage;
        this.storage = userStorage;
    }

    @Override
    public List<User> getUserFriends(long id) {
        return friendshipStorage.getFriends(id).keySet().stream()
                .map(super::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public User addToFriends(long userId, long friendId) {
        if (checkIds(userId, friendId)) {
            User user = super.getUser(userId);
            if (user.addFriend(friendId, false)) {
                friendshipStorage.addFriend(userId, friendId);
                log.info("Пользователь id = {} добавил в друзья пользователя id = {}", userId, friendId);
                return user;
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
        if (checkIds(userId, friendId)) {
            User user = super.getUser(userId);
            if (user.removeFriend(friendId)) {
                friendshipStorage.removeFriend(userId, friendId);
                log.info("Пользователь id = {} удалил из друзей пользователя id = {}", userId, friendId);
                return "Пользователь удален из друзей";
            } else {
                log.info("Неудачное удаление из друзей, пользователи " +
                        "id = {} и id = {} ими не являются", userId, friendId);
                throw new StorageException("Невозможно удалить пользователя из друзей, вы ими не являетесь");
            }
        }

        log.warn("Ошибка операции пользователя id = {} и/или id = {} не существует", userId, friendId);
        throw new StorageException("Невозможно выполнить удаление, одного из пользователей не существует");
    }

    @Override
    public User confirmFriendship(Long userId, Long friendId) {
        if (checkIds(userId, friendId)) {
            if (friendshipStorage.confirmFriendship(userId, friendId)) {
                return super.getUser(userId);
            } else {
                log.warn("Ошибка операции. Нет записи о дружбе id = {} и/или id = {}", userId, friendId);
                throw new StorageException("Невозможно подтвердить дружбу, одного из пользователей не существует");
            }

        }
        log.warn("Ошибка операции пользователя id = {} и/или id = {} не существует", userId, friendId);
        throw new ValidationException("Невозможно выполнить действие, одного из пользователей не существует");
    }

    @Override
    public List<User> getMutualFriends(long userId, long friendId) {
        return friendshipStorage.getMutualFriends(userId, friendId).stream()
                .map(super::getUser)
                .collect(Collectors.toList());
    }

    private boolean checkIds(long userId, long friendId) {
        return !Objects.equals(userId, friendId)
                && storage.userExist(userId)
                && storage.userExist(friendId);
    }
}