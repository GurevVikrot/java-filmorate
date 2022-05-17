package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserControllerTest {
    private UserController controller;
    private User user;

    @BeforeEach
    void beforeEach() {
        controller = new UserController();
        user = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
    }

    @Test
    void correctCreateTest() {
        controller.createUser(user);
        User userInController = controller.getUsers().get(0);
        assertEquals(user, userInController);
    }

    @Test
    void createAlreadyExistUser() {
        controller.createUser(user);
        User user2 = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
        user2.setId(controller.getUsers().get(0).getId());
        assertThrows(ValidationException.class, () -> controller.createUser(user2));
    }

    @Test
    void withoutNameUserTest() {
        user.setName(null);
        controller.createUser(user);

        User userWithLoginName = new User("a@mail.ru", "login", "login", LocalDate.of(1990, 10, 10));
        userWithLoginName.setId(controller.getUsers().get(0).getId());
        assertEquals(userWithLoginName, controller.getUsers().get(0));
    }

    @Test
    void correctUpdateTest() {
        controller.createUser(user);
        User userInController = controller.getUsers().get(0);
        User userToUpdate = new User("a@mail.ru", "login", "anotherName", LocalDate.of(2000, 1, 1));
        userToUpdate.setId(userInController.getId());
        controller.updateUser(userToUpdate);
        assertEquals(userToUpdate, controller.getUsers().get(0));
    }

    @Test
    void updateNotExistUserTest() {
        assertThrows(ValidationException.class, () -> controller.updateUser(user));
    }

    @Test
    void updateWithNullIdTest() {
        user.setId(null);
        assertThrows(ValidationException.class, () -> controller.updateUser(user));
    }

    @Test
    void GetTest() {
        assertTrue(controller.getUsers().isEmpty());

        controller.createUser(user);
        List<User> users1 = controller.getUsers();
        assertEquals(1, users1.size());

        User userToUpdate = new User("a@mail.ru", "login", "anotherName", LocalDate.of(2000, 1, 1));
        controller.createUser(userToUpdate);
        List<User> users2 = controller.getUsers();
        assertEquals(2, users2.size());
    }

    @Test
    void setIdTest() {
        assertNull(user.getId());

        controller.createUser(user);
        User userInController = controller.getUsers().get(0);
        assertNotNull(userInController.getId());

        user.setId(null);
        controller.createUser(user);
        User userInController2 = controller.getUsers().get(1);
        assertNotNull(userInController2.getId());
    }

    @Test
    void UserLoginWithSpacesTest() {
        User userTester = new User("a@mail.ru", " f ", "name", LocalDate.of(1990, 10, 10));
        assertThrows(ValidationException.class, () -> controller.createUser(userTester));
    }
}