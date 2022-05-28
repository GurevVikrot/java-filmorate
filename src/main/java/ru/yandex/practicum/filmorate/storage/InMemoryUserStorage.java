package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public boolean createUser(User user) {
        if (!users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteUser(Integer id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public User getUser(Integer userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean userExist(Integer id) {
        return users.containsKey(id);
    }
}