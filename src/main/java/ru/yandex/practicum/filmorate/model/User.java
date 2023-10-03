package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User extends Entity {
    @Email
    @NotEmpty
    private String email;
    @NotBlank
    @Pattern(regexp = "\\S*")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.setId(id);
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
