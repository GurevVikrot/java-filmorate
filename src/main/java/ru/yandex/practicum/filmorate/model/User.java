package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Data
@NotNull
public class User {
    private final Set<Long> friends = new TreeSet<>();

    @NotBlank(message = "login не может быть пустым")
    private final String login;
    private long id;

    @NotNull(message = "email не может быть пустым")
    @NotBlank
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

    public boolean addFriend(long id) {
        return friends.add(id);
    }

    public boolean removeFriend(long id) {
        return friends.remove(id);
    }

    public List<Long> getFriendsId() {
        return new ArrayList<>(friends);
    }
}