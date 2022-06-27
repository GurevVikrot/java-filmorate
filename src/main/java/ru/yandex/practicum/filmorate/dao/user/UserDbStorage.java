package ru.yandex.practicum.filmorate.dao.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.StorageException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DAO класс для работы с таблицей users БД
 */

@Component
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipDAO friendshipDAO;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendshipDAO friendshipDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendshipDAO = friendshipDAO;
    }

    // Создание пользователя в БД
    @Override
    public Optional<User> createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        long userId = (simpleJdbcInsert.executeAndReturnKey(userToMap(user)).longValue());
        return getUser(userId);
    }

    // Обновление данных о пользователе. id, login являются не изменяемыми.
    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET login = ?, email = ?, name = ?, birthday = ? WHERE user_id = ?";

        if (user == null) {
            throw new StorageException("Получен null для обновления");
        }

        return jdbcTemplate.update(sql,
                user.getLogin(),
                user.getEmail(),
                user.getName(),
                user.getBirthday(),
                user.getId()) > 0;
    }

    // Удаление пользователя по id
    @Override
    public boolean deleteUser(long id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public Optional<User> getUser(long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> createUserFromDb(rs), userId);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(0));
    }

    // Получение списка всех пользователей
    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> createUserFromDb(rs));
    }

    // Проверка наличия записи о пользователе. Нужна для проставления лайков фильму.
    @Override
    public boolean userExist(long id) {
        String sql = "SELECT user_id FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForRowSet(sql, id).next();
    }

    // Создание User из ResultSet полученного из БД
    private User createUserFromDb(ResultSet rs) throws SQLException {
        User user = new User(rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate());
        user.setId(rs.getInt("user_id"));

        // Заполняем список друзей пользователя и статус дружбы
        Map<Long, Boolean> friends = friendshipDAO.getFriends(user.getId());
        for (Long id : friends.keySet()) {
            user.addFriend(id, friends.get(id));
        }

        return user;
    }

    // Преобразование User в Map<> для использования в SimpleJdbcInsert
    private Map<String, Object> userToMap(User user) {
        if (user == null) {
            throw new StorageException("Получен null для сохранения");
        }

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", user.getEmail());
        userMap.put("login", user.getLogin());
        userMap.put("name", user.getName());
        userMap.put("birthday", user.getBirthday());
        return userMap;
    }
}
