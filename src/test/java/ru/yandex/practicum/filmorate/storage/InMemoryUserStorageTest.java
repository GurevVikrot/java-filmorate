package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryUserStorageTest {
    private UserStorage storage;
    private User user;

    @BeforeEach
    void beforeEach() {
        storage = new InMemoryUserStorage();
        user = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
    }

    @Test
    void createUserTest() {
        assertTrue(storage.getAllUsers().isEmpty());
        assertTrue(storage.createUser(user).isPresent());

        User user1 = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
        user1.setId(user.getId());

        assertFalse(storage.createUser(user).isPresent());
        assertEquals(1, storage.getAllUsers().size());
        assertEquals(user1, storage.getUser(user.getId()).orElse(null));
    }

    @Test
    void updateUserTest() {
        storage.createUser(user);
        User user1 = new User("a@mail.ru", "up", "date", LocalDate.of(1990, 10, 10));
        user1.setId(user.getId());
        assertTrue(storage.updateUser(user1));
        assertEquals(user1, storage.getUser(user1.getId()).orElse(null));
    }

    @Test
    void updateNonexistentUser() {
        assertFalse(storage.updateUser(user));
        assertTrue(storage.getAllUsers().isEmpty());
    }

    @Test
    void deleteUserTest() {
        storage.createUser(user);
        assertFalse(storage.getAllUsers().isEmpty());

        assertTrue(storage.deleteUser(user.getId()));
        assertTrue(storage.getAllUsers().isEmpty());
    }

    @Test
    void deleteNonexistentUser() {
        assertFalse(storage.deleteUser(user.getId()));
        assertTrue(storage.getAllUsers().isEmpty());
    }

    @Test
    void getUserTest() {
        storage.createUser(user);
        assertEquals(user, storage.getUser(user.getId()).orElse(null));
        assertFalse(storage.getUser(-1).isPresent());
    }

    @Test
    void getAllUsersTest() {
        assertEquals(0, storage.getAllUsers().size());
        storage.createUser(user);
        User user1 = new User("1a@mail.ru", "1log", "1name", LocalDate.of(1990, 10, 10));
        User user2 = new User("2a@mail.ru", "2log", "2name", LocalDate.of(1990, 10, 10));
        storage.createUser(user1);
        storage.createUser(user2);
        assertEquals(3, storage.getAllUsers().size());
    }

    @Test
    void userExistTest() {
        assertFalse(storage.userExist(user.getId()));
        storage.createUser(user);
        assertTrue(storage.userExist(user.getId()));
    }
}