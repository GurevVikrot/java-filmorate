package ru.yandex.practicum.filmorate.dao.user;

import java.util.List;
import java.util.Map;

public interface FriendshipDAO {
    Map<Long, Boolean> getFriends(long userId);

    boolean addFriend(long userId, long friendId);

    boolean removeFriend(long userId, long friendId);

    List<Long> getMutualFriends(long userId, long friendId);

    boolean confirmFriendship(long userId, long friendId);
}