package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NotNull
public class User {
    private final Map<Long, Boolean> friends = new HashMap<>();

    @NotBlank(message = "login не может быть пустым")
    private String login;
    private long id;


    @NotBlank(message = "email не может быть пустым")
    @Email(message = "не верный формат email")
    private String email;
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public boolean addFriend(long id, boolean status) {
        if (friends.containsKey(id)) {
            return false;
        }
        friends.put(id, status);
        return true;
    }

    public boolean removeFriend(long id) {
        return friends.remove(id) != null;
    }

    public List<Long> getFriendsId() {
        return new ArrayList<>(friends.keySet());
    }
}