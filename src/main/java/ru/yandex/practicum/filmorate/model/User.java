package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User extends Entity {
    @Email
    @NotEmpty
    private String email;
    private String name;
    @NotBlank
    @Pattern(regexp = "\\S*")
    private String login;
    @PastOrPresent
    private LocalDate birthday;
    private Set<Integer> friends;

    public User(int id, String email, String name, String login, LocalDate birthday) {
        this.setId(id);
        this.email = email;
        this.name = name;
        this.login = login;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }

    public void addFriends(int id) {
        friends.add(id);
    }

    public void deleteFriends(Integer id) {
        friends.remove(id);
    }
}
