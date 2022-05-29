package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    private static Validator validator;
    private static ValidatorFactory factory;
    private User user;

    @BeforeAll
    public static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
    }

    @BeforeEach
    void beforeEach() {
        user = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
        validator = factory.getValidator();
    }

    @Test
    void correctEmailTest() {
        assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    void incorrectEmailTest() {
        user.setEmail("mail.ru");
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void emptyEmailTest() {
        user.setEmail("");
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void nullEmailTest() {
        user.setEmail(null);
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void correctLoginTest() {
        assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    void nullLoginTest() {
        User userTester = new User("a@mail.ru", null, "name", LocalDate.of(1990, 10, 10));
        assertFalse(validator.validate(userTester).isEmpty());
    }

    @Test
    void incorrectLoginTest() {
        User userTester = new User("a@mail.ru", " ", "name", LocalDate.of(1990, 10, 10));
        assertFalse(validator.validate(userTester).isEmpty());
    }

    @Test
    void correctBirthdayTest() {
        assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    void futureBirthdayTest() {
        user.setBirthday(LocalDate.of(2045, 10, 10));
        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void addFriendTest() {
        assertTrue(user.addFriend(0));
        assertTrue(user.addFriend(1));
        assertTrue(user.addFriend(2));
    }

    @Test
    void deleteFriendTest() {
        assertTrue(user.addFriend(1));
        assertTrue(user.removeFriend(1));
        assertFalse(user.removeFriend(0));
    }

    @Test
    void getFriendsTest() {
        assertTrue(user.getFriendsId().isEmpty());

        user.addFriend(0);
        user.addFriend(1);
        user.addFriend(2);
        user.addFriend(3);
        assertEquals(4, user.getFriendsId().size());

        user.removeFriend(0);
        user.removeFriend(1);
        user.removeFriend(2);
        user.addFriend(3);
        assertEquals(1, user.getFriendsId().size());
        assertEquals(3, user.getFriendsId().get(0));
    }
}