package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.util.StorageException;
import ru.yandex.practicum.filmorate.util.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultUserServiceTest {
    private UserService userService;
    private User user;

    @BeforeEach
    void beforeEach() {
        userService = new DefaultUserService(new InMemoryUserStorage());
        user = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
    }

    @Test
    void correctCreateTest() {
        userService.createUser(user);
        User user1 = userService.getUsers().get(0);
        assertEquals(user, user1);
    }

    @Test
    void createAlreadyExistUser() {
        userService.createUser(user);
        User user2 = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
        user2.setId(userService.getUsers().get(0).getId());
        assertThrows(ValidationException.class, () -> userService.createUser(user2));
    }

    @Test
    void withoutNameUserTest() {
        user.setName(null);
        userService.createUser(user);

        User userWithLoginName = new User("a@mail.ru", "login", "login", LocalDate.of(1990, 10, 10));
        userWithLoginName.setId(userService.getUsers().get(0).getId());
        assertEquals(userWithLoginName, userService.getUsers().get(0));
    }

    @Test
    void correctUpdateTest() {
        userService.createUser(user);
        User userInuserService = userService.getUsers().get(0);
        User userToUpdate = new User("a@mail.ru", "login", "anotherName", LocalDate.of(2000, 1, 1));
        userToUpdate.setId(userInuserService.getId());
        userService.updateUser(userToUpdate);
        assertEquals(userToUpdate, userService.getUsers().get(0));
    }

    @Test
    void updateNotExistUserTest() {
        user.setId(0);
        assertThrows(StorageException.class, () -> userService.updateUser(user));
    }

    @Test
    void updateWithNullIdTest() {
        user.setId(0);
        assertThrows(StorageException.class, () -> userService.updateUser(user));
    }

    @Test
    void getAllUsersTest() {
        assertTrue(userService.getUsers().isEmpty());

        userService.createUser(user);
        List<User> users1 = userService.getUsers();
        assertEquals(1, users1.size());

        User userToUpdate = new User("a@mail.ru", "login", "anotherName", LocalDate.of(2000, 1, 1));
        userService.createUser(userToUpdate);
        List<User> users2 = userService.getUsers();
        assertEquals(2, users2.size());
    }

    @Test
    void getUserTest() {
        assertThrows(StorageException.class, () -> userService.getUser(-1));
        assertThrows(StorageException.class, () -> userService.getUser(0));

        userService.createUser(user);
        assertEquals(user, userService.getUser(user.getId()));
    }

    @Test
    void setIdTest() {
        assertEquals(0, user.getId());

        userService.createUser(user);
        User userInUserService = userService.getUsers().get(0);
        assertTrue(userInUserService.getId() > 0);

        user.setId(0);
        userService.createUser(user);
        User userInUserService2 = userService.getUsers().get(1);
        assertTrue(userInUserService2.getId() > 0);
    }

    @Test
    void userLoginWithSpacesTest() {
        User userTester = new User("a@mail.ru", " f ", "name", LocalDate.of(1990, 10, 10));
        assertThrows(ValidationException.class, () -> userService.createUser(userTester));
    }

    @Test
    void correctDeleteUser() {
        assertTrue(userService.getUsers().isEmpty());
        userService.createUser(user);
        assertFalse(userService.getUsers().isEmpty());

        userService.deleteUser(user.getId());
        assertTrue(userService.getUsers().isEmpty());
    }

    @Test
    void deleteNonexistentUser() {
        assertThrows(StorageException.class, () -> userService.deleteUser(-1));
        assertTrue(userService.getUsers().isEmpty());

        assertThrows(StorageException.class, () -> userService.deleteUser(0));
        assertTrue(userService.getUsers().isEmpty());
    }

    @Test
    void correctAddToFriendAndDelete() {
        User userFriend = new User("friend@mail.ru", "FriendLogin", "Friend", LocalDate.of(2000, 1, 1));
        userService.createUser(user);
        userService.createUser(userFriend);
        userService.addToFriends(user.getId(), userFriend.getId());
        assertEquals(user, userService.getUserFriends(userFriend.getId()).get(0));
        assertEquals(userFriend, userService.getUserFriends(user.getId()).get(0));

        userService.removeFromFriends(user.getId(), userFriend.getId());
        assertTrue(userService.getUserFriends(user.getId()).isEmpty());
        assertTrue(userService.getUserFriends(userFriend.getId()).isEmpty());
    }

    @Test
    void addAndRemoveFromFriendsSelf() {
        userService.createUser(user);
        assertThrows(StorageException.class, () -> userService.addToFriends(user.getId(), user.getId()));
        assertThrows(StorageException.class, () -> userService.removeFromFriends(user.getId(), user.getId()));
    }

    @Test
    void addToFriendNonexistentUser() {
        assertThrows(StorageException.class, () -> userService.addToFriends(-1, -2));
        userService.createUser(user);
        assertThrows(StorageException.class, () -> userService.addToFriends(user.getId(), -2));
        assertThrows(StorageException.class, () -> userService.addToFriends(-1, user.getId()));
    }

    @Test
    void alreadyFriendsAdd() {
        User userFriend = new User("friend@mail.ru", "FriendLogin", "Friend", LocalDate.of(2000, 1, 1));
        userService.createUser(user);
        userService.createUser(userFriend);
        userService.addToFriends(user.getId(), userFriend.getId());
        assertThrows(StorageException.class, () -> userService.addToFriends(user.getId(), userFriend.getId()));
    }

    @Test
    void removeFromFriendsNonexistentUser() {
        assertThrows(StorageException.class, () -> userService.removeFromFriends(-1, -2));
        userService.createUser(user);
        assertThrows(StorageException.class, () -> userService.removeFromFriends(user.getId(), -2));
        assertThrows(StorageException.class, () -> userService.removeFromFriends(-1, user.getId()));
    }

    @Test
    void removeFromFriendsWhenNotFriends() {
        User userFriend = new User("friend@mail.ru", "FriendLogin", "Friend", LocalDate.of(2000, 1, 1));
        userService.createUser(user);
        userService.createUser(userFriend);
        assertThrows(StorageException.class, () -> userService.removeFromFriends(user.getId(), userFriend.getId()));
    }

    @Test
    void getUserFriendsTest() {
        User userFriend = new User("friend@mail.ru", "FriendLogin", "Friend", LocalDate.of(2000, 1, 1));
        userService.createUser(user);
        userService.createUser(userFriend);
        assertTrue(userService.getUserFriends(user.getId()).isEmpty());
        assertTrue(userService.getUserFriends(userFriend.getId()).isEmpty());

        userService.addToFriends(user.getId(), userFriend.getId());
        assertFalse(userService.getUserFriends(user.getId()).isEmpty());
        assertEquals(userFriend, userService.getUserFriends(user.getId()).get(0));
        assertFalse(userService.getUserFriends(userFriend.getId()).isEmpty());
        assertEquals(user, userService.getUserFriends(userFriend.getId()).get(0));
    }

    @Test
    void getNonexistentUserFriends() {
        assertThrows(StorageException.class, () -> userService.getUserFriends(-1));
    }

    @Test
    void getMutualFriendsTest() {
        User mainFriend = new User("1friend@mail.ru", "1FriendLogin", "Friend", LocalDate.of(2000, 1, 1));
        User userFriend2 = new User("2friend@mail.ru", "2FriendLogin", "Friend", LocalDate.of(2000, 1, 1));
        User userFriend3 = new User("3friend@mail.ru", "3FriendLogin", "Friend", LocalDate.of(2000, 1, 1));
        User userFriend4 = new User("4friend@mail.ru", "4FriendLogin", "Friend", LocalDate.of(2000, 1, 1));
        User userFriend5 = new User("5friend@mail.ru", "5FriendLogin", "Friend", LocalDate.of(2000, 1, 1));
        User userFriend6 = new User("6friend@mail.ru", "6FriendLogin", "Friend", LocalDate.of(2000, 1, 1));
        User userFriend7 = new User("7friend@mail.ru", "7FriendLogin", "Friend", LocalDate.of(2000, 1, 1));
        User userFriend8 = new User("8friend@mail.ru", "8FriendLogin", "Friend", LocalDate.of(2000, 1, 1));
        User userFriend9 = new User("9friend@mail.ru", "9FriendLogin", "Friend", LocalDate.of(2000, 1, 1));

        userService.createUser(user);
        userService.createUser(mainFriend);
        userService.createUser(userFriend2);
        userService.createUser(userFriend3);
        userService.createUser(userFriend4);
        userService.createUser(userFriend5);
        userService.createUser(userFriend6);
        userService.createUser(userFriend7);
        userService.createUser(userFriend8);
        userService.createUser(userFriend9);

        userService.addToFriends(user.getId(), mainFriend.getId());
        userService.addToFriends(user.getId(), userFriend2.getId());
        userService.addToFriends(user.getId(), userFriend3.getId());
        userService.addToFriends(user.getId(), userFriend4.getId());
        userService.addToFriends(user.getId(), userFriend5.getId());
        userService.addToFriends(user.getId(), userFriend6.getId());
        userService.addToFriends(user.getId(), userFriend7.getId());
        userService.addToFriends(user.getId(), userFriend8.getId());
        userService.addToFriends(user.getId(), userFriend9.getId());

        userService.addToFriends(mainFriend.getId(), userFriend2.getId());
        userService.addToFriends(mainFriend.getId(), userFriend3.getId());
        userService.addToFriends(mainFriend.getId(), userFriend4.getId());
        userService.addToFriends(mainFriend.getId(), userFriend5.getId());

        List<User> userFriends = List.of(mainFriend, userFriend2, userFriend3, userFriend4, userFriend5, userFriend6,
                userFriend7, userFriend8, userFriend9);
        List<User> friendFriends = List.of(user, userFriend2, userFriend3, userFriend4, userFriend5);
        List<User> mutualFriends = List.of(userFriend2, userFriend3, userFriend4, userFriend5);

        assertEquals(userFriends, userService.getUserFriends(user.getId()));
        assertEquals(friendFriends, userService.getUserFriends(mainFriend.getId()));
        assertEquals(mutualFriends, userService.getMutualFriends(user.getId(), mainFriend.getId()));
        assertEquals(userFriends, userService.getUserFriends(user.getId()));
        assertEquals(friendFriends, userService.getUserFriends(mainFriend.getId()));
    }
}