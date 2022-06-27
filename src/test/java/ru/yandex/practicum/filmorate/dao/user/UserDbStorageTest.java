package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.StorageException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User("a@mail.ru", "login", "name", LocalDate.of(1990, 10, 10));
    }

    @Test
    public void createUser() {
        Optional<User> userOptional = userStorage.createUser(user);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
        user.setId(1);
        assertEquals(user, userOptional.get());
    }

    @Test
    public void createUserWithId() {
        user.setId(-1);
        Optional<User> userOptional = userStorage.createUser(user);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
        user.setId(1);
        assertEquals(user, userOptional.get());

        user.setId(5);
        Optional<User> userOptional2 = userStorage.createUser(user);
        assertThat(userOptional2)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 2L));
        user.setId(2);
        assertEquals(user, userOptional2.get());
    }

    @Test
    public void createAndUpdateNullUser() {
        assertThrows(StorageException.class, () -> userStorage.createUser(null));
        assertThrows(StorageException.class, () -> userStorage.updateUser(null));
    }

    @Test
    public void update() {
        userStorage.createUser(user);
        user.setEmail("up@date.ru");
        user.setName("up");
        user.setBirthday(LocalDate.of(2000, 2, 2));
        user.setLogin("updated");
        user.setId(1);

        assertTrue(userStorage.updateUser(user));
        Optional<User> updatedUser = userStorage.getUser(user.getId());
        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
        assertEquals(user, updatedUser.get());
    }

    @Test
    public void updateNonexistentUser() {
        userStorage.updateUser(user);
        assertThat(userStorage.getUser(1))
                .isEmpty();
    }

    @Test
    public void deleteUser() {
        userStorage.createUser(user);
        assertTrue(userStorage.deleteUser(1L));
        assertThat(userStorage.getUser(1))
                .isEmpty();
    }

    @Test
    public void deleteNonexistentUser() {
        assertFalse(userStorage.deleteUser(1L));
        assertFalse(userStorage.deleteUser(-1L));
    }

    @Test
    public void getUserTest() {
        userStorage.createUser(user);
        Optional<User> userOptional = userStorage.getUser(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void getNonexistentUser() {
        assertThat(userStorage.getUser(1))
                .isEmpty();
        assertThat(userStorage.getUser(-1))
                .isEmpty();
    }

    @Test
    public void getAllUsersTest() {
        assertTrue(userStorage.getAllUsers().isEmpty());

        Optional<User> user1 = userStorage.createUser(user);
        Optional<User> user2 = userStorage.createUser(user);
        Optional<User> user3 = userStorage.createUser(user);

        List<User> users = userStorage.getAllUsers();

        assertNotNull(users);
        assertEquals(3, users.size());
        assertEquals(user1.orElse(user), users.get(0));
        assertEquals(user2.orElse(user), users.get(1));
        assertEquals(user3.orElse(user), users.get(2));
    }

    @Test
    public void userExistTest() {
        assertFalse(userStorage.userExist(1));
        userStorage.createUser(user);
        assertTrue(userStorage.userExist(1));
    }
}