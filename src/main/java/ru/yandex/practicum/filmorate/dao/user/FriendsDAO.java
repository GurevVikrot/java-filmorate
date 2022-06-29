package ru.yandex.practicum.filmorate.dao.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO реализация для работы с таблицей user_friends
 */
@Component
public class FriendsDAO implements FriendshipDAO {
    private final JdbcTemplate jdbcTemplate;

    public FriendsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, Boolean> getFriends(long userId) {
        String sql = "SELECT friend_id, status FROM user_friends WHERE user_id = ?";

        return jdbcTemplate.query(sql, rs -> {
            Map<Long, Boolean> friends = new HashMap<>();
            while (rs.next()) {
                friends.put(rs.getLong("friend_id"), rs.getBoolean("status"));
            }
            return friends;
        }, userId);
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user_friends");

        Map<String, Object> friendshipMap = new HashMap<>();
        friendshipMap.put("user_id", userId);
        friendshipMap.put("friend_id", friendId);
        friendshipMap.put("status", false);
        return insert.execute(friendshipMap) > 0;
    }

    @Override
    public boolean removeFriend(long userId, long friendId) {
        String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public List<Long> getMutualFriends(long userId, long friendId) {
        String sql = "SELECT friend_id FROM user_friends WHERE user_id = ? AND friend_id IN" +
                "(SELECT friend_id FROM user_friends WHERE user_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friend_id"), userId, friendId);
    }

    @Override
    public boolean confirmFriendship(long userId, long friendId) {
        String sql = "UPDATE user_friends SET status = ? WHERE user_id = ? AND friend_id = ?";
        String check = "SELECT friend_id FROM user_friends WHERE user_id = ? AND friend_id = ?";

        // Проверяем наличие дружбы между пользователями
        if (jdbcTemplate.queryForRowSet(check, userId, friendId).next() &&
                jdbcTemplate.queryForRowSet(check, friendId, userId).next()) {

            // Обновляем записи
            return jdbcTemplate.update(sql, true, userId, friendId) > 0
                    && jdbcTemplate.update(sql, true, friendId, userId) > 0;
        }

        return false;
    }
}