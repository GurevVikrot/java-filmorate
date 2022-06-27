package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FriendsDAOTest {
    private final FriendsDAO friendsDAO;
    private final UserDbStorage userDbStorage;

    @BeforeEach
    void beforeEach() {
        User user = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
        User friend = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
        userDbStorage.createUser(user);
        userDbStorage.createUser(friend);
    }

    @Test
    public void CorrectAddAndGetFriends() {
        Map<Long, Boolean> friendsEmpty = friendsDAO.getFriends(1);
        assertNotNull(friendsEmpty);
        assertTrue(friendsDAO.getFriends(1).isEmpty());

        friendsDAO.addFriend(1, 2);
        Map<Long, Boolean> friendsAdded12 = friendsDAO.getFriends(1);
        assertNotNull(friendsAdded12);
        assertFalse(friendsAdded12.isEmpty());
        assertEquals(1, friendsAdded12.size());
        assertTrue(friendsAdded12.containsKey(2L));
        assertFalse(friendsAdded12.get(2L));

        friendsDAO.addFriend(2, 1);
        Map<Long, Boolean> friendsAdded21 = friendsDAO.getFriends(2);
        assertNotNull(friendsAdded21);
        assertFalse(friendsAdded21.isEmpty());
        assertEquals(1, friendsAdded21.size());
        assertTrue(friendsAdded21.containsKey(1L));
        assertFalse(friendsAdded21.get(1L));
    }

    @Test
    public void addWhenIdsEquals() {
        assertThrows(DataIntegrityViolationException.class, () -> friendsDAO.addFriend(1, 1));
    }

    @Test
    public void addNotExistUserOrFriend() {
        assertThrows(DataIntegrityViolationException.class, () -> friendsDAO.addFriend(1, 4));
        assertThrows(DataIntegrityViolationException.class, () -> friendsDAO.addFriend(4, 1));
        assertThrows(DataIntegrityViolationException.class, () -> friendsDAO.addFriend(3, 4));
    }

    @Test
    public void getNonExistFriends() {
        assertNotNull(friendsDAO.getFriends(-1));
        assertTrue(friendsDAO.getFriends(-1).isEmpty());
        assertNotNull(friendsDAO.getFriends(1));
        assertTrue(friendsDAO.getFriends(1).isEmpty());
    }

    @Test
    public void removeFriendCorrect() {
        friendsDAO.addFriend(1, 2);
        Map<Long, Boolean> friendsAdded12 = friendsDAO.getFriends(1);
        assertNotNull(friendsAdded12);
        assertFalse(friendsAdded12.isEmpty());
        assertEquals(1, friendsAdded12.size());
        assertTrue(friendsAdded12.containsKey(2L));
        assertFalse(friendsAdded12.get(2L));

        friendsDAO.removeFriend(1, 2);
        assertNotNull(friendsDAO.getFriends(1));
        assertTrue(friendsDAO.getFriends(1).isEmpty());
    }

    @Test
    public void incorrectRemove() {
        assertFalse(friendsDAO.removeFriend(1, 2));
    }

    @Test
    public void getMutualFriends() {
        User user3 = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
        User user4 = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
        User user5 = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
        userDbStorage.createUser(user3);
        userDbStorage.createUser(user4);
        userDbStorage.createUser(user5);

        assertNotNull(friendsDAO.getMutualFriends(1, 2));
        assertTrue(friendsDAO.getMutualFriends(1, 2).isEmpty());

        friendsDAO.addFriend(1, 2);
        friendsDAO.addFriend(1, 3);
        friendsDAO.addFriend(1, 4);
        friendsDAO.addFriend(1, 5);

        friendsDAO.addFriend(2, 1);
        friendsDAO.addFriend(2, 3);
        friendsDAO.addFriend(2, 4);
        friendsDAO.addFriend(2, 5);

        List<Long> mutualFriends = List.of(3L, 4L, 5L);

        assertNotNull(friendsDAO.getMutualFriends(1, 2));
        assertFalse(friendsDAO.getMutualFriends(1, 2).isEmpty());
        assertEquals(mutualFriends, friendsDAO.getMutualFriends(1, 2));
    }

    @Test
    public void confirmFriend() {
        friendsDAO.addFriend(1, 2);
        friendsDAO.addFriend(2, 1);

        assertTrue(friendsDAO.confirmFriendship(1, 2));
        assertTrue(friendsDAO.getFriends(1).get(2L));
        assertTrue(friendsDAO.getFriends(2).get(1L));
    }

    @Test
    public void NotConfirmFriendIfOneSide() {
        friendsDAO.addFriend(1, 2);
        assertFalse(friendsDAO.confirmFriendship(1, 2));
        assertFalse(friendsDAO.getFriends(1).get(2L));
        assertTrue(friendsDAO.getFriends(2).isEmpty());
    }
}