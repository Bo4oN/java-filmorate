package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class User extends Entity {
    @Email
    private String email;
    private String name;
    @NotNull
    private String login;
    private LocalDate birthday;

    public User(int id, String email, String name, String login, LocalDate birthday) {
        this.setId(id);
        this.email = email;
        this.name = name;
        this.login = login;
        this.birthday = birthday;
    }
}
